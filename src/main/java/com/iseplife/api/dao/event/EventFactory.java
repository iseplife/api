package com.iseplife.api.dao.event;

import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.event.Event;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Component
public class EventFactory {
  static public Event dtoToEntity(EventDTO dto) {
    Event event = new Event();
    event.setTitle(dto.getTitle());
    event.setStart(dto.getStartsAt());
    event.setEnd(dto.getEndsAt());
    event.setPrice(dto.getPrice());
    event.setLocation(dto.getLocation());
    event.setDescription(dto.getDescription());
    event.setPublished(dto.getPublished());
    return event;
  }

  static public Event dtoToEntity(EventDTO dto, Event previous) {
    Event event = new Event();
    event.setTitle(dto.getTitle() != null ? dto.getTitle() : previous.getTitle());
    event.setStart(dto.getStartsAt() != null ? dto.getStartsAt() : previous.getStart());
    event.setEnd(dto.getEndsAt() != null ? dto.getEndsAt() : previous.getEnd());
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
    //preview.setTarget(event.getTarget() == null ? null: event.getTarget().getName());
    preview.setStart(event.getStart());
    preview.setEnd(event.getEnd());
    preview.setImageUrl(event.getImageUrl());
    preview.setPublished(event.getPublished());
    return preview;
  }

}
