package com.iseplife.api.services;

import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  private final int EVENTS_PER_PAGE = 10;


  public Event createEvent(MultipartFile image, EventDTO dto) {
    Event event = eventFactory.dtoToEntity(dto);
    Club club = clubService.getClub(dto.getClubId());
    //TODO: Possibly useless because when we check rights we check club's id
    if (club == null) {
      throw new IllegalArgumentException("Could not find a club with id: " + dto.getClubId());
    }
    if (dto.getPreviousEditionId() != null) {
      Event previous = getEvent(dto.getPreviousEditionId());
      event = eventFactory.dtoToEntity(dto, previous);
    } else {
      event = eventFactory.dtoToEntity(dto);
    }

    String path = createImageEvent(image);

    event.setFeed(new Feed());
    event.setImageUrl(path);
    return eventRepository.save(event);
  }

  private String createImageEvent(MultipartFile image) {
    Map params = ObjectUtils.asMap(
      "process", "resize",
      "sizes", ""
    );
    return fileHandler.upload(image, "/img", false, params);
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
      .findAroundDate(date, token.getRoles().contains("ROLE_ADMIN") )
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public Page<EventPreviewView> getFutureEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findFutureEvents(token.getRoles().contains("ROLE_ADMIN"), date, PageRequest.of(page, EVENTS_PER_PAGE))
      .map(e -> eventFactory.entityToPreviewView(e));
  }

  public Page<EventPreviewView> getPassedEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findPassedEvents(token.getRoles().contains("ROLE_ADMIN"), date, PageRequest.of(page, EVENTS_PER_PAGE))
      .map(e -> eventFactory.entityToPreviewView(e));
  }


  public Event getEvent(Long id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isEmpty())
      throw new IllegalArgumentException("could not find event with id: " + id);

    return event.get();
  }

  public List<Gallery> getEventGalleries(Long id) {
    return galleryService.getEventGalleries(getEvent(id));
  }

  public List<EventPreviewView> getChildrenEvents(Long id) {
    Event event = getEvent(id);
    if (event == null) {
      throw new IllegalArgumentException("could not find event with id: " + id);
    }

    return event.getEvents()
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public List<EventPreviewView> getPreviousEditions(Long id) {
    Event event = getEvent(id);
    List<Event> previousEditions = new LinkedList<>();
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
      Event prev = getEvent(eventDTO.getPreviousEditionId());
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
    eventRepository.deleteById(id);
  }
}
