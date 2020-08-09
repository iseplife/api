package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dto.EventDTO;
import com.iseplife.api.dto.view.EventPreviewView;
import com.iseplife.api.dto.view.EventView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.services.EventService;
import com.iseplife.api.services.SubscriptionService;
import com.iseplife.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@RestController
@RequestMapping("/event")
public class EventController {
  @Autowired
  EventService eventService;
  
  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public EventView createEvent(@RequestBody EventDTO dto) {
    return eventService.createEvent(dto);
  }

  @PostMapping("/{id}/image")
  @RolesAllowed({Roles.STUDENT})
  public String updateLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    return eventService.updateImage(id, file);
  }

  @GetMapping("/m/{timestamp}")
  @RolesAllowed({Roles.STUDENT})
  public  List<EventPreviewView> getMonthEvents(@PathVariable Long timestamp, @AuthenticationPrincipal TokenPayload token) {
    return eventService.getMonthEvents(new Date(timestamp), token);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public EventView getEvent(@PathVariable Long id) {
    return eventService.getEventView(id);
  }

  @GetMapping("/{id}/previous")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewView> getPreviousEditions(@PathVariable Long id) {
    return eventService.getPreviousEditions(id);
  }

  @GetMapping("/{id}/children")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewView> getChildrenEvents(@PathVariable Long id) {
    return eventService.getChildrenEvents(id);
  }

  @GetMapping("/{id}/galleries")
  @RolesAllowed({Roles.STUDENT})
  public List<Gallery> getGalleries(@PathVariable Long id) {
    return eventService.getEventGalleries(id);
  }


  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Event updateEvent(@PathVariable Long id, @RequestBody EventDTO dto) {
    return eventService.updateEvent(id, dto);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public void deleteEvent(@PathVariable Long id) {
    eventService.removeEvent(id);
  }

}
