package com.iseplife.api.services;


import com.google.common.collect.Sets;
import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class EventService {

  @Autowired
  ClubService clubService;

  @Autowired
  GalleryService galleryService;

  @Autowired
  SubscriptionService subscriptionService;

  @Autowired
  FeedService feedService;

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
    if (club == null || !SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");


    Event event = dto.getPreviousEditionId() == null ?
      EventFactory.dtoToEntity(dto) :
      EventFactory.dtoToEntity(dto, getEvent(dto.getPreviousEditionId()));

    event.setClub(club);
    event.setFeed(new Feed(dto.getTitle()));
    if (dto.getTargets().size() > 0)
      event.setTargets(Sets.newHashSet(feedRepository.findAllById(dto.getTargets())));

    return EventFactory.toView(eventRepository.save(event), false);
  }

  public String updateImage(Long id, MultipartFile file) {
    Event event = getEvent(id);
    Map params = Map.of(
      "process", "compress",
      "sizes", StorageConfig.MEDIAS_CONF.get("feed_cover").sizes
    );

    if (event.getCover() != null)
      fileHandler.delete(event.getCover());

    event.setCover(fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("feed_cover").path, false, params));
    eventRepository.save(event);

    return event.getCover();
  }


  public List<EventPreview> getEvents() {
    return eventRepository.findAll()
      .stream()
      .map(EventFactory::toPreview)
      .collect(Collectors.toList());
  }


  public List<EventPreviewProjection> getMonthEvents(Date date, TokenPayload token) {
    return eventRepository.findAllInMonth(date, token.getRoles().contains("ROLE_ADMIN"), token.getFeeds());
  }

  public List<EventPreviewProjection> getIncomingEvents(TokenPayload token, Long feed) {
    if (feed != 1)
      return getFeedIncomingEvents(token, feedService.getFeed(feed));

    return eventRepository.findIncomingEvents(
      token.getRoles().contains("ROLE_ADMIN"),
      token.getFeeds(),
      PageRequest.of(0, EVENTS_PER_PAGE)
    ).toList();
  }

  public List<EventPreviewProjection> getFeedIncomingEvents(TokenPayload token, Feed feed) {
    if (!SecurityService.hasReadAccess(feed))
      throw new HttpNotFoundException("feed_not_found");

    return eventRepository.findFeedIncomingEvents(
      token.getRoles().contains("ROLE_ADMIN"),
      feed,
      PageRequest.of(0, EVENTS_PER_PAGE)
    ).toList();
  }


  private Event getEvent(Long id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isEmpty() || !SecurityService.hasReadAccessOn(event.get()))
      throw new HttpNotFoundException("not_found");

    return event.get();
  }

  public EventView getEventView(Long id) {
    Event event = getEvent(id);

    return EventFactory.toView(event, feedService.isSubscribedToFeed(event));
  }

  public Page<GalleryPreview> getEventGalleries(Long id, int page) {
    return galleryService.getEventGalleries(getEvent(id), page);
  }

  public List<EventPreviewProjection> getChildrenEvents(Long id) {
    Event event = getEvent(id);

    return event.getChildren()
      .stream()
      .map(EventFactory::toPreview)
      .collect(Collectors.toList());
  }

  public List<EventPreviewProjection> getPreviousEditions(Long id) {
    Event event = getEvent(id);
    List<Event> previousEditions = new LinkedList<>();

    //Only return the 5 last edition
    for (int i = 0; i < 5; i++) {
      event = event.getPreviousEdition();
      if (event != null) {
        previousEditions.add(event);
      } else break;
    }

    return previousEditions.stream()
      .map(EventFactory::toPreview)
      .collect(Collectors.toList());
  }

  public Event updateEvent(Long id, EventDTO dto) {
    Event event = getEvent(id);
    if (!SecurityService.hasRightOn(event))
      throw new HttpForbiddenException("insufficient_rights");

    event.setTitle(dto.getTitle());
    event.setDescription(dto.getDescription());
    event.setLocation(dto.getLocation());
    event.setCoordinates(dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1]);
    event.setStartsAt(dto.getStartsAt());
    if (dto.getPreviousEditionId() != null) {
      Event prev = getEvent(dto.getPreviousEditionId());
      event.setPreviousEdition(prev);
    }

    return eventRepository.save(event);
  }

  public void removeEvent(Long id) {
    Event event = getEvent(id);
    if (event.getCover() != null) {
      fileHandler.delete(event.getCover());
    }
    eventRepository.deleteById(id);
  }
}
