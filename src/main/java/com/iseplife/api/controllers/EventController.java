package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.services.EventService;
import com.iseplife.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@RestController
@RequestMapping("/event")
public class EventController {

  @Autowired
  EventService eventService;

  @Autowired
  JsonUtils jsonUtils;

  private boolean hasRights(TokenPayload payload, Long clubId) {
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      // if user is not the club admin
      return !payload.getClubsAdmin().contains(clubId);
    }
    return false;
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Event createEvent(@RequestParam("image") MultipartFile file,
                           @RequestParam("event") String event,
                           @AuthenticationPrincipal TokenPayload auth) {
    EventDTO eventDTO = jsonUtils.deserialize(event, EventDTO.class);
    if (hasRights(auth, eventDTO.getClubId())) {
      throw new AuthException("you are not this club's admin");
    }
    return eventService.createEvent(file, eventDTO);
  }

  @GetMapping
  public Map<Long, List<EventPreviewView>> getCurrentEvents(@AuthenticationPrincipal TokenPayload auth) {
    return eventService.getTodayEvents(auth);
  }

  @GetMapping("/{timestamp}")
  public Map<Long, List<EventPreviewView>> getEventsAroundDate(@AuthenticationPrincipal TokenPayload auth, @PathVariable Long timestamp) {
    return eventService.getAroundDateEvents(auth, new Date(timestamp));
  }

  @GetMapping("/{timestamp}/future")
  public Map<Long, List<EventPreviewView>> getAllFutureEvents(@AuthenticationPrincipal TokenPayload auth, @PathVariable Long timestamp, @RequestParam(defaultValue = "0") int page) {
    return eventService.getFutureEvents(auth, new Date(timestamp), page);
  }

  @GetMapping("/{timestamp/previous")
  public Map<Long, List<EventPreviewView>> getAllPassedEvents(@AuthenticationPrincipal TokenPayload auth, @PathVariable Long timestamp, @RequestParam(defaultValue = "0") int page) {
    return eventService.getPassedEvents(auth, new Date(timestamp), page);
  }

  @GetMapping("/{id}")
  public Event getEvent(@PathVariable Long id) {
    return eventService.getEvent(id);
  }

  @GetMapping("/{id}/previous")
  public List<EventPreviewView> getPreviousEditions(@PathVariable Long id) {
    return eventService.getPreviousEditions(id);
  }

  @GetMapping("/{id}/children")
  public List<EventPreviewView> getChildrenEvents(@PathVariable Long id) {
    return eventService.getChildrenEvents(id);
  }

  @GetMapping("/{id}/galleries")
  public List<Gallery> getGalleries(@PathVariable Long id) {
    return eventService.getEventGalleries(id);
  }


  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Event updateEvent(@PathVariable Long id,
                           @RequestParam(value = "image", required = false) MultipartFile file,
                           @RequestParam("event") String event,
                           @AuthenticationPrincipal TokenPayload auth) {
    EventDTO eventDTO = jsonUtils.deserialize(event, EventDTO.class);
    if (hasRights(auth, eventDTO.getClubId())) {
      throw new AuthException("you are not this club's admin");
    }
    return eventService.updateEvent(id, eventDTO, file);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public void deleteEvent(@PathVariable Long id) {
    eventService.removeEvent(id);
  }

}