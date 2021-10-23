package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.embed.view.media.MediaNameView;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
  final private EventService eventService;

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public EventView createEvent(@RequestBody EventDTO dto) {
    return eventService.createEvent(dto);
  }

  @PutMapping("/{id}/image")
  @RolesAllowed({Roles.STUDENT})
  public MediaNameView updateCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    return MediaFactory.toNameView(eventService.updateImage(id, file));
  }

  @GetMapping("/m/{timestamp}")
  @RolesAllowed({Roles.STUDENT})
  public  List<EventPreviewProjection> getMonthEvents(@PathVariable Long timestamp, @AuthenticationPrincipal TokenPayload token) {
    return eventService.getMonthEvents(new Date(timestamp), token);
  }

  @GetMapping("/incoming")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewProjection> getIncomingEvents(@AuthenticationPrincipal TokenPayload token, @RequestParam(name = "feed", defaultValue = "1") Long feed) {
    return eventService.getIncomingEvents(token, feed);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public EventView getEvent(@PathVariable Long id) {
    return eventService.getEventView(id);
  }

  @GetMapping("/{id}/previous")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewProjection> getPreviousEditions(@PathVariable Long id) {
    return eventService.getPreviousEditions(id);
  }

  @GetMapping("/{id}/children")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewProjection> getChildrenEvents(@PathVariable Long id) {
    return eventService.getChildrenEvents(id);
  }

  @GetMapping("/{id}/galleries")
  @RolesAllowed({Roles.STUDENT})
  public Page<GalleryPreview> getGalleries(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return eventService.getEventGalleries(id, page);
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
