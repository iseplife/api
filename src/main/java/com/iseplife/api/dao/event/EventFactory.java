package com.iseplife.api.dao.event;

import com.iseplife.api.dto.media.EventDTO;
import com.iseplife.api.entity.Event;
import com.iseplife.api.dto.media.EventDTO;
import com.iseplife.api.entity.Event;
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
    event.setStartsAt(dto.getDate());
    event.setLocation(dto.getLocation());
    event.setDescription(dto.getDescription());
    return event;
  }
}
