package com.iseplife.api.dao.event;

import com.iseplife.api.constants.EventType;
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.feed.FeedFactory;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.services.SecurityService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class EventFactory {

  static public Event dtoToEntity(EventDTO dto) {
    Event event = new Event();
    event.setTitle(dto.getTitle());
    event.setType(EventType.valueOf(dto.getType()));
    event.setDescription(dto.getDescription());
    event.setStartsAt(dto.getStartsAt());
    event.setEndsAt(dto.getEndsAt());
    event.setPrice(dto.getPrice());
    event.setTicketUrl(dto.getTicketUrl());
    event.setLocation(dto.getLocation());

    // Generic version: Stream.of(dto.getCoordinates()).map(String::valueOf).collect(Collectors.joining(";")
    event.setCoordinates(dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1]);

    event.setClosed(dto.getClosed());
    event.setPublishedAt(dto.getPublished());
    return event;
  }

  static public Event dtoToEntity(EventDTO dto, Event previous) {
    Event event = dtoToEntity(dto);
    event.setPreviousEdition(previous);

    event.setTitle(dto.getTitle() != null ? dto.getTitle() : previous.getTitle());
    event.setStartsAt(dto.getStartsAt() != null ? dto.getStartsAt() : previous.getStartsAt());
    event.setEndsAt(dto.getEndsAt() != null ? dto.getEndsAt() : previous.getEndsAt());
    event.setPrice(dto.getPrice() != null ? dto.getPrice() : previous.getPrice());
    event.setLocation(dto.getLocation() != null ? dto.getLocation() : previous.getLocation());

    event.setCoordinates(dto.getLocation() != null ?
      dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1] :
      previous.getCoordinates()
    );

    return event;
  }

  static public EventPreview toPreview(Event event) {
    EventPreview preview = new EventPreview();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setType(event.getType().name());
    preview.setTargets(event.getTargets().stream().map(Feed::getId).collect(Collectors.toSet()));
    preview.setStartsAt(event.getStartsAt());
    preview.setEndsAt(event.getEndsAt());
    preview.setCover(event.getCover());
    preview.setPublished(event.getPublishedAt().before(new Date()));
    return preview;
  }

  static public EventView toView(Event event, Boolean isSubscribed) {
    EventView view = new EventView();
    view.setId(event.getId());
    view.setType(event.getType().name());
    view.setTitle(event.getTitle());
    view.setDescription(event.getDescription());
    view.setCover(event.getCover());

    view.setStartsAt(event.getStartsAt());
    view.setEndsAt(event.getEndsAt());
    view.setLocation(event.getLocation());

    // Split string containing long & lag and parsing it into float
    if (event.getCoordinates() != null)
      view.setCoordinates(Arrays.stream(event.getCoordinates().split(";")).map(Float::valueOf).toArray(Float[]::new));
    view.setTicketURL(event.getTicketUrl());
    view.setPrice(event.getPrice());
    view.setPublished(event.getPublishedAt());
    view.setClosed(event.isClosed());

    view.setSubscribed(isSubscribed);
    view.setFeed(event.getFeed().getId());
    view.setTargets(event.getTargets().stream().map(FeedFactory::toView).collect(Collectors.toSet()));
    view.setHasRight(SecurityService.hasRightOn(event));
    view.setClub(ClubFactory.toPreview(event.getClub()));
    return view;
  }

}
