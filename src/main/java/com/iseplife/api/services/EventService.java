package com.iseplife.api.services;


import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.group.FeedRepository;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.dto.view.EventView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import com.iseplife.api.utils.ObjectUtils;
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
  AuthService authService;

  @Autowired
  ClubService clubService;

  @Autowired
  GalleryService galleryService;

  @Autowired
  SubscriptionService subscriptionService;

  @Autowired
  FeedRepository feedRepository;

  @Autowired
  EventRepository eventRepository;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  private final int EVENTS_PER_PAGE = 10;

  public EventView createEvent(EventDTO dto) {
    Club club = clubService.getClub(dto.getClub());
    if (club == null || !AuthService.hasRightOn(club))
      throw new AuthException("Insufficient rights for club (" + dto.getClub() + ")");


    Event event = dto.getPreviousEditionId() == null ?
      EventFactory.dtoToEntity(dto) :
      EventFactory.dtoToEntity(dto, getEvent(dto.getPreviousEditionId()));

    event.setClub(club);
    event.setFeed(new Feed());
    if(dto.getTargets().size() > 0)
      event.setTargets((Set<Feed>) feedRepository.findAllById(dto.getTargets()));

    return EventFactory.toView(eventRepository.save(event), false);
  }

  public String updateImage(Long id, MultipartFile file) {
    Event event = getEvent(id);
    Map params = ObjectUtils.asMap(
      "process", "resize",
      "sizes", ""
    );

    if (event.getImageUrl() != null)
      fileHandler.delete(event.getImageUrl());

    event.setImageUrl(fileHandler.upload(file, "/img", false, params));
    eventRepository.save(event);

    return event.getImageUrl();
  }


  public List<EventPreviewView> getEvents() {
    return eventRepository.findAll()
      .stream()
      .map(EventFactory::entityToPreviewView)
      .collect(Collectors.toList());
  }


  public List<EventPreviewView> getMonthEvents(Date date, TokenPayload token) {
    return eventRepository.findAllInMonth(date, token.getRoles().contains("ROLE_ADMIN"), token.getFeeds())
      .stream()
      .map(EventFactory::entityToPreviewView)
      .collect(Collectors.toList());
  }

  public List<EventPreviewView> getTodayEvents(TokenPayload token) {
    return getAroundDateEvents(token, new Date());
  }

  public List<EventPreviewView> getAroundDateEvents(TokenPayload token, Date date) {
    return eventRepository
      .findAroundDate(date, token.getRoles().contains("ROLE_ADMIN"))
      .stream()
      .map(EventFactory::entityToPreviewView)
      .collect(Collectors.toList());
  }

  public Page<EventPreviewView> getFutureEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findFutureEvents(token.getRoles().contains("ROLE_ADMIN"), date, PageRequest.of(page, EVENTS_PER_PAGE))
      .map(EventFactory::entityToPreviewView);
  }

  public Page<EventPreviewView> getPassedEvents(TokenPayload token, Date date, int page) {
    return eventRepository
      .findPassedEvents(token.getRoles().contains("ROLE_ADMIN"), date, PageRequest.of(page, EVENTS_PER_PAGE))
      .map(EventFactory::entityToPreviewView);
  }


  private Event getEvent(Long id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isEmpty())
      throw new IllegalArgumentException("could not find event with id: " + id);

    return event.get();
  }

  public EventView getEventView(Long id) {
    Event e = getEvent(id);

    return EventFactory.toView(e, subscriptionService.isSubscribedToFeed(e.getFeed().getId()));
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
      .map(EventFactory::entityToPreviewView)
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
      .map(EventFactory::entityToPreviewView)
      .collect(Collectors.toList());
  }

  public Event updateEvent(Long id, EventDTO eventDTO) {
    Event event = getEvent(id);
    if (!AuthService.hasRightOn(event))
      throw new AuthException("You are not allowed to edit this event");

    event.setTitle(eventDTO.getTitle());
    event.setDescription(eventDTO.getDescription());
    event.setLocation(eventDTO.getLocation());
    event.setStart(eventDTO.getStart());
    if (eventDTO.getPreviousEditionId() != null) {
      Event prev = getEvent(eventDTO.getPreviousEditionId());
      event.setPreviousEdition(prev);
    }

    return eventRepository.save(event);
  }

  public void removeEvent(Long id) {
    Event event = getEvent(id);
    if (event.getImageUrl() != null) {
      fileHandler.delete(event.getImageUrl());
    }
    eventRepository.deleteById(id);
  }
}
