package com.iseplife.api.services;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.FeedType;
import com.iseplife.api.constants.NotificationType;
import com.iseplife.api.dao.event.EventTabPreviewProjection;
import com.iseplife.api.dao.event.EventPositionRepository;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.dao.event.PositionRequestAPIResponse;
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
import com.iseplife.api.websocket.services.WsEventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
  private static String REVERSE_URL = "https://api-adresse.data.gouv.fr/reverse/?lon=%s&lat=%s";
  
  @Lazy final private ClubService clubService;
  @Lazy final private GalleryService galleryService;
  @Lazy final private FeedService feedService;
  final private ModelMapper mapper;
  final private FeedRepository feedRepository;
  final private EventRepository eventRepository;
  final private NotificationService notificationService;
  final private EventPositionRepository eventPositionRepository;
  final private WebClient http = WebClient.builder().build();
  final private WsEventService wsEventService;
  
  @Qualifier("FileHandlerBean") final private FileHandler fileHandler;

  final private static int EVENTS_PER_PAGE = 10;

  public Event createEvent(EventDTO dto) {
    Club club = clubService.getClub(dto.getClub());
    if (club == null || !SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    Event event = mapper.map(dto, Event.class);

    event.setClub(club);
    event.setFeed(new Feed(dto.getTitle(), FeedType.EVENT));
     
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
          false, club, () -> eventRepository.findByIdWithPosition(finalEvent.getId()) != null);
    }
    
    wsEventService.broadcastEvent(event);
    
    return event;
  }

  private void updateCoordinates(Event event, EventDTO dto) {
    event.setLocation(dto.getLocation());
    
    if(dto.getCoordinates() != null) {
      PositionRequestAPIResponse coordinatesData = http.get()
          .uri(
            String.format(
              REVERSE_URL,
              Double.valueOf(dto.getCoordinates()[1]),
              Double.valueOf(dto.getCoordinates()[0])
            )
          )
          .retrieve()
          .bodyToMono(PositionRequestAPIResponse.class).block();
      
      EventPosition position = coordinatesData.features.get(0).properties;
      position.setCoordinates(dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1]);
      
      eventPositionRepository.save(position);
      
      event.setPosition(position);
    } else
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
  public Page<EventTabPreviewProjection> getEventsFrom(TokenPayload token, Long clubId, int page) {
      return eventRepository.findFrom(
          clubId,
          token.getRoles().contains("ROLE_ADMIN"),
          token.getFeeds(),
          PageRequest.of(page, EVENTS_PER_PAGE, Sort.by(Direction.DESC, "startsAt"))
      );
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
    Optional<Event> event = eventRepository.findByIdWithPosition(id);
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
