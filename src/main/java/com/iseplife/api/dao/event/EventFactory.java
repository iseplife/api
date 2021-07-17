package com.iseplife.api.dao.event;

import com.iseplife.api.constants.EventType;
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreview;
import com.iseplife.api.dto.view.EventView;
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
    event.setStart(dto.getStart());
    event.setEnd(dto.getEnd());
    event.setPrice(dto.getPrice());
    event.setTicketUrl(dto.getTicketUrl());
    event.setLocation(dto.getLocation());

    // Generic version: Stream.of(dto.getCoordinates()).map(String::valueOf).collect(Collectors.joining(";")
    event.setCoordinates(dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1]);

    event.setClosed(dto.getClosed());
    event.setPublished(dto.getPublished());
    return event;
  }

  static public Event dtoToEntity(EventDTO dto, Event previous) {
    Event event = dtoToEntity(dto);
    event.setPreviousEdition(previous);

    event.setTitle(dto.getTitle() != null ? dto.getTitle() : previous.getTitle());
    event.setStart(dto.getStart() != null ? dto.getStart() : previous.getStart());
    event.setEnd(dto.getEnd() != null ? dto.getEnd() : previous.getEnd());
    event.setPrice(dto.getPrice() != null ? dto.getPrice() : previous.getPrice());
    event.setLocation(dto.getLocation() != null ? dto.getLocation() : previous.getLocation());

    event.setCoordinates(dto.getLocation() != null ?
      dto.getCoordinates()[0] + ";" + dto.getCoordinates()[1] :
      previous.getCoordinates()
    );

    return event;
  }

  static public EventPreview entityToPreviewView(Event event) {
    EventPreview preview = new EventPreview();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setType(event.getType().name());
    preview.setTargets(event.getTargets().stream().map(Feed::getId).collect(Collectors.toSet()));
    preview.setStart(event.getStart());
    preview.setEnd(event.getEnd());
    preview.setCover(event.getImageUrl());
    preview.setPublished(event.getPublished().before(new Date()));
    return preview;
  }

  static public EventView toView(Event event, Boolean isSubscribed) {
    EventView view = new EventView();
    view.setId(event.getId());
    view.setType(event.getType().name());
    view.setTitle(event.getTitle());
    view.setDescription(event.getDescription());
    view.setCover(event.getImageUrl());

    view.setStart(event.getStart());
    view.setEnd(event.getEnd());
    view.setLocation(event.getLocation());

    // Split string containing long & lag and parsing it into float
    if (event.getCoordinates() != null)
      view.setCoordinates(Arrays.stream(event.getCoordinates().split(";")).map(Float::valueOf).toArray(Float[]::new));
    view.setTicketURL(event.getTicketUrl());
    view.setPrice(event.getPrice());
    view.setPublished(event.getPublished());
    view.setClosed(event.getClosed());

    view.setSubscribed(isSubscribed);
    view.setFeed(event.getFeed().getId());
    view.setTargets(event.getTargets());
    view.setHasRight(SecurityService.hasRightOn(event));
    view.setClub(ClubFactory.toPreview(event.getClub()));
    return view;
  }

}
