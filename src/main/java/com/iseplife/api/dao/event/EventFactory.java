package com.iseplife.api.dao.event;

import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.event.Event;
import org.springframework.stereotype.Component;

import java.util.*;

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
    event.setTitle(dto.getTitle() != null ? dto.getTitle() : previous.getTitle());
    event.setStartsAt(dto.getStartsAt() != null ? dto.getStartsAt() : previous.getStartsAt());
    event.setEndsAt(dto.getEndsAt() != null ? dto.getEndsAt() : previous.getEndsAt());
    event.setPrice(dto.getPrice() != null ? dto.getPrice() : previous.getPrice());
    event.setLocation(dto.getLocation() != null ? dto.getLocation() : previous.getLocation());
    event.setDescription(dto.getDescription() != null ? dto.getDescription() : previous.getDescription());
    event.setPreviousEdition(previous);
    event.setVisible(dto.getVisible());
    return event;
  }

  public EventPreviewView entityToPreviewView(Event event) {
    EventPreviewView preview = new EventPreviewView();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setStartsAt(event.getStartsAt());
    preview.setEndsAt(event.getEndsAt());
    preview.setImageUrl(event.getImageUrl());
    return preview;
  }

  public Map<Long, List<EventPreviewView>> iterableToDateMap(Iterable<Event> events) {
    Map<Long, List<EventPreviewView>> map = new HashMap<>();
    final Calendar calendar = Calendar.getInstance();
    for (Event e : events) {
      EventPreviewView preview = entityToPreviewView(e);
      calendar.setTime(preview.getStartsAt());
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (map.putIfAbsent(calendar.getTimeInMillis(), Collections.singletonList(preview)) != null) {
        List<EventPreviewView> previews = map.get(calendar.getTimeInMillis());
        previews.add(preview);
        map.put(calendar.getTimeInMillis(), previews);
      }
    }
    return map;
  }
}