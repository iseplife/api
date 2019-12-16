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
  public Event dtoToEntity(EventDTO dto) {
    Event event = new Event();
    event.setTitle(dto.getTitle());
    event.setStartsAt(dto.getStartsAt());
    event.setEndsAt(dto.getEndsAt());
    event.setPrice(dto.getPrice());
    event.setLocation(dto.getLocation());
    event.setDescription(dto.getDescription());
    event.setVisible(dto.getVisible());
    return event;
  }

  public Event dtoToEntity(EventDTO dto, Event previous) {
    Event event = new Event();
    event.setTitle(dto.getTitle() != null ? dto.getTitle(): previous.getTitle());
    event.setStartsAt(dto.getStartsAt() != null ? dto.getStartsAt(): previous.getStartsAt());
    event.setEndsAt(dto.getEndsAt() != null ? dto.getEndsAt(): previous.getEndsAt());
    event.setPrice(dto.getPrice() != null ? dto.getPrice(): previous.getPrice());
    event.setLocation(dto.getLocation() !=  null ? dto.getLocation(): previous.getLocation());
    event.setDescription(dto.getDescription() != null ? dto.getDescription(): previous.getDescription());
    event.setPreviousEdition(previous);
    event.setVisible(dto.getVisible());
    return event;
  }

  public EventPreviewView entityToPreviewView(Event event){
    EventPreviewView preview = new EventPreviewView();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setStartsAt(event.getStartsAt());
    preview.setEndsAt(event.getEndsAt());
    preview.setImageUrl(event.getImageUrl());
    return preview;
  }
}
