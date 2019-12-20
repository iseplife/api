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
import com.iseplife.api.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
  MediaUtils mediaUtils;

  @Autowired
  PostService postService;

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

    String eventPath = createImageEvent(image);

    event.setImageUrl(mediaUtils.getPublicUrlImage(eventPath));
    event = eventRepository.save(event);

    Feed eventFeed = new Feed();
    //TODO: slugify name
    eventFeed.setName(event.getTitle());
    event.setFeed(eventFeed);
    return event;
  }

  private String createImageEvent(MultipartFile image) {
    String random = mediaUtils.randomName();
    String eventPath = mediaUtils.resolvePath(
      eventBaseUrl, random, false);
    mediaUtils.saveJPG(image, WIDTH_EVENT_IMAGE, eventPath);
    return eventPath;
  }

  public List<EventPreviewView> getEvents() {
    return eventRepository.findAll()
      .stream()
      .map(e -> eventFactory.entityToPreviewView(e))
      .collect(Collectors.toList());
  }

  public Map<Long, List<EventPreviewView>> getTodayEvents(TokenPayload token) {
    return getAroundDateEvents(token, new Date());
  }

  public Map<Long, List<EventPreviewView>> getAroundDateEvents(TokenPayload token, Date date) {
    return eventFactory.iterableToDateMap(
      eventRepository.findAroundDate(token.getFeed(), token.getRoles().contains("ROLE_ADMIN"), date)
    );
  }

  public Map<Long, List<EventPreviewView>> getFutureEvents(TokenPayload token, Date date, int page) {
    return eventFactory.iterableToDateMap(
      eventRepository.findFutureEvents(token.getFeed(), token.getRoles().contains("ROLE_ADMIN"), date, createPage(page))
    );
  }

  public Map<Long, List<EventPreviewView>> getPassedEvents(TokenPayload token, Date date, int page) {
    return eventFactory.iterableToDateMap(
      eventRepository.findPassedEvents(token.getFeed(), token.getRoles().contains("ROLE_ADMIN"), date, createPage(page))
    );
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
      String eventPath = createImageEvent(file);
      mediaUtils.removeIfExistPublic(event.getImageUrl());
      event.setImageUrl(mediaUtils.getPublicUrlImage(eventPath));
    }

    return eventRepository.save(event);
  }

  public void removeEvent(Long id) {
    Event event = getEvent(id);
    mediaUtils.removeIfExistPublic(event.getImageUrl());
    eventRepository.delete(id);
  }
}