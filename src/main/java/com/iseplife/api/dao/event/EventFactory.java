package com.iseplife.api.dao.event;

import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Component
public class EventFactory {
  static public Event dtoToEntity(EventDTO dto) {
    Event event = new Event();
    event.setTitle(dto.getTitle());
    event.setStart(dto.getStart());
    event.setEnd(dto.getEnd());
    event.setPrice(dto.getPrice());
    event.setLocation(dto.getLocation());
    event.setDescription(dto.getDescription());
    event.setPublished(dto.getPublished());
    return event;
  }

  static public Event dtoToEntity(EventDTO dto, Event previous) {
    Event event = new Event();
    event.setTitle(dto.getTitle() != null ? dto.getTitle() : previous.getTitle());
    event.setStart(dto.getStart() != null ? dto.getStart() : previous.getStart());
    event.setEnd(dto.getEnd() != null ? dto.getEnd() : previous.getEnd());
    event.setPrice(dto.getPrice() != null ? dto.getPrice() : previous.getPrice());
    event.setLocation(dto.getLocation() != null ? dto.getLocation() : previous.getLocation());
    event.setDescription(dto.getDescription() != null ? dto.getDescription() : previous.getDescription());
    event.setPreviousEdition(previous);
    event.setPublished(dto.getPublished());
    return event;
  }

  static public EventPreviewView entityToPreviewView(Event event) {
    EventPreviewView preview = new EventPreviewView();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setType(event.getType().name());
    preview.setTargets(event.getTargets().stream().map(Feed::getId).collect(Collectors.toSet()));
    preview.setStart(event.getStart());
    preview.setEnd(event.getEnd());
    preview.setImageUrl(event.getImageUrl());
    preview.setPublished(event.getPublished());
    return preview;
  }

}
