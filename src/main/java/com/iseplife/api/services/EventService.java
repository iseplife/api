package com.iseplife.api.services;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.NotificationType;
import com.iseplife.api.dao.event.EventPositionRepository;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.dao.event.PositionRequestResponse;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.event.EventPosition;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
  @Lazy final private ClubService clubService;
  @Lazy final private GalleryService galleryService;
  @Lazy final private FeedService feedService;
  final private ModelMapper mapper;
  final private FeedRepository feedRepository;
  final private EventRepository eventRepository;
  final private NotificationService notificationService;
  private final RestTemplateBuilder restTemplateBuilder;
  private RestTemplate restTemplate;
  private final EventPositionRepository eventPositionRepository;
  
  @PostConstruct
  private void init() {
    restTemplate = restTemplateBuilder.build();
  }

  @Qualifier("FileHandlerBean") final private FileHandler fileHandler;

  final private static int EVENTS_PER_PAGE = 10;

  public Event createEvent(EventDTO dto) {
    Club club = clubService.getClub(dto.getClub());
    if (club == null || !SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    Event event = mapper.map(dto, Event.class);

    event.setClub(club);
    event.setFeed(new Feed(dto.getTitle()));
     
    updateCoordinates(event, dto);
    
    if (dto.getTargets().size() > 0) {
      Set<Feed> targets = new HashSet<>();
      feedRepository.findAllById(dto.getTargets()).forEach(targets::add);

      event.setTargets(targets);
      event = eventRepository.save(event);
    } else {
      event = eventRepository.save(event);
      Event finalEvent = event;
      //We send the notification if the event is not private
      notificationService.delayNotification(
          Notification.builder()
            .type(NotificationType.NEW_EVENT)
            .icon(club.getLogoUrl())
            .link("event/"+event.getId())
            .informations(
                Map.of(
                    "id", event.getId(),
                    "title", event.getTitle(),
                    "type", event.getType().name(),
                    "club", event.getClub().getName(),
                    "start", event.getStartsAt().getTime()
                )
            ),
          false, club, () -> eventRepository.findById(finalEvent.getId()) != null);
    }

    return event;
  }

  private void updateCoordinates(Event event, EventDTO dto) {
    if(dto.getCoordinates() != null) {
      PositionRequestResponse coordinatesData = restTemplate.getForObject("https://api-adresse.data.gouv.fr/reverse/?lon=" + Double.valueOf(dto.getCoordinates()[1]) + "&lat=" + Double.valueOf(dto.getCoordinates()[0]), PositionRequestResponse.class);
      
      EventPosition position = coordinatesData.features.get(0).properties;
      position.setLocation(dto.getLocation());
      position.setCoordinates(dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1]);
      
      eventPositionRepository.save(position);
      
      event.setPosition(position);
    }else
      event.setPosition(null);
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


  public List<Event> getEvents() {
    return eventRepository.findAll();
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


  public Event getEvent(Long id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isEmpty() || !SecurityService.hasReadAccessOn(event.get()))
      throw new HttpNotFoundException("not_found");

    return event.get();
  }

  public Page<Gallery> getEventGalleries(Long id, int page) {
    return galleryService.getEventGalleries(getEvent(id), page);
  }

  public List<Event> getChildrenEvents(Long id) {
    return getEvent(id).getChildren();
  }

  public List<Event> getPreviousEditions(Long id) {
    Event event = getEvent(id);
    List<Event> previousEditions = new LinkedList<>();

    //Only return the 5 last edition
    for (int i = 0; i < 5; i++) {
      event = event.getPreviousEdition();
      if (event != null) {
        previousEditions.add(event);
      } else break;
    }

    return previousEditions;
  }

  public Event updateEvent(Long id, EventDTO dto) {
    Event event = getEvent(id);
    if (!SecurityService.hasRightOn(event))
      throw new HttpForbiddenException("insufficient_rights");

    event.setTitle(dto.getTitle());
    event.setDescription(dto.getDescription());
    
    updateCoordinates(event, dto);
    
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
