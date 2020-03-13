package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Service
public class EventService {
  @Autowired
  EventRepository eventRepository;

  @Autowired
  EventFactory eventFactory;

  @Autowired
  ClubService clubService;

  @Autowired
  GalleryService galleryService;

  @Autowired
  PostService postService;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  @Value("${storage.event.url}")
  String eventBaseUrl;

  private static final int WIDTH_EVENT_IMAGE = 1024;

  private final int EVENTS_PER_PAGE = 10;

  private Pageable createPage(int page) {
    return new PageRequest(page, EVENTS_PER_PAGE);
  }

  public Event createEvent(MultipartFile image, EventDTO dto) {
    Event event = eventFactory.dtoToEntity(dto);
    Club club = clubService.getClub(dto.getClubId());
    //TODO: Possibly useless because when we check rights we check club's id
    if (club == null) {
      throw new IllegalArgumentException("Could not find a club with id: " + dto.getClubId());
    }
    if (dto.getPreviousEditionId() != null) {
      Event previous = eventRepository.findOne(dto.getPreviousEditionId());
      event = eventFactory.dtoToEntity(dto, previous);
    } else {
      event = eventFactory.dtoToEntity(dto);
    }

    String path = createImageEvent(image);

    event.setImageUrl(path);
    event = eventRepository.save(event);

    Feed eventFeed = new Feed();
    //TODO: slugify name
    eventFeed.setName(event.getTitle());
    event.setFeed(eventFeed);
    return event;
  }

  private String createImageEvent(MultipartFile image) {
    return fileHandler.upload(image, "/img/", Collections.EMPTY_MAP);
  }

  public List<EventPreviewView> getEvents() {
    return eventRepository.findAll()
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public List<EventPreviewView> getTodayEvents(TokenPayload token) {
    return getAroundDateEvents(token, new Date());
  }

  public List<EventPreviewView> getAroundDateEvents(TokenPayload token, Date date) {
    return eventRepository
      .findAroundDate(date, token.getRoles().contains("ROLE_ADMIN"), token.getFeed())
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public Page<EventPreviewView> getFutureEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findFutureEvents(token.getFeed(), token.getRoles().contains("ROLE_ADMIN"), date, createPage(page))
      .map(e -> eventFactory.entityToPreviewView(e));
  }

  public Page<EventPreviewView> getPassedEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findPassedEvents(token.getFeed(), token.getRoles().contains("ROLE_ADMIN"), date, createPage(page))
      .map(e -> eventFactory.entityToPreviewView(e));
  }


  public Event getEvent(Long id) {
    Event event = eventRepository.findOne(id);
    if (event == null) {
      throw new IllegalArgumentException("could not find event with id: " + id);
    }
    return event;
  }

  public List<Gallery> getEventGalleries(Long id) {
    return galleryService.getEventGalleries(getEvent(id));
  }

  public List<EventPreviewView> getChildrenEvents(Long id) {
    Event event = eventRepository.findOne(id);
    if (event == null) {
      throw new IllegalArgumentException("could not find event with id: " + id);
    }

    return event.getEvents()
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public List<EventPreviewView> getPreviousEditions(Long id) {
    Event event = eventRepository.findOne(id);
    List<Event> previousEditions = new LinkedList<Event>();
    if (event == null) {
      throw new IllegalArgumentException("could not find event with id: " + id);
    }

    //Only return the 5 last edition
    for (int i = 0; i < 5; i++) {
      event = event.getPreviousEdition();
      if (event != null) {
        previousEditions.add(event);
      } else break;
    }

    return previousEditions.stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public Event updateEvent(Long id, EventDTO eventDTO, MultipartFile file) {
    Event event = getEvent(id);

    event.setTitle(eventDTO.getTitle());
    event.setDescription(eventDTO.getDescription());
    event.setLocation(eventDTO.getLocation());
    event.setStartsAt(eventDTO.getStartsAt());
    if (eventDTO.getPreviousEditionId() != null) {
      Event prev = eventRepository.findOne(eventDTO.getPreviousEditionId());
      event.setPreviousEdition(prev);
    }
    if (file != null) {
      if(event.getImageUrl() != null){
        fileHandler.delete(event.getImageUrl());
      }

      event.setImageUrl(createImageEvent(file));
    }

    return eventRepository.save(event);
  }

  public void removeEvent(Long id) {
    Event event = getEvent(id);
    if(event.getImageUrl() != null){
      fileHandler.delete(event.getImageUrl());
    }
    eventRepository.delete(id);
  }
}
