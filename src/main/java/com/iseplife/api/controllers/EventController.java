package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.dto.media.view.MediaNameView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.services.EventService;
import com.iseplife.api.services.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
  final private FeedService feedService;
  final private EventService eventService;
  final private EventFactory factory;
  final private GalleryFactory galleryFactory;

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public EventView createEvent(@RequestBody EventDTO dto) {
    Event event = eventService.createEvent(dto);
    return factory.toView(event, feedService.isSubscribedToFeed(event));
  }

  @PutMapping("/{id}/image")
  @RolesAllowed({Roles.STUDENT})
  public MediaNameView updateCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    return MediaFactory.toNameView(eventService.updateImage(id, file));
  }

  @GetMapping("/m/{timestamp}")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreviewProjection> getMonthEvents(@PathVariable Long timestamp, @AuthenticationPrincipal TokenPayload token) {
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
    Event event = eventService.getEvent(id);
    return factory.toView(event, feedService.isSubscribedToFeed(event));
  }

  @GetMapping("/{id}/previous")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreview> getPreviousEditions(@PathVariable Long id) {
    return eventService.getPreviousEditions(id).stream()
      .map(factory::toPreview)
      .collect(Collectors.toList());
  }

  @GetMapping("/{id}/children")
  @RolesAllowed({Roles.STUDENT})
  public List<EventPreview> getChildrenEvents(@PathVariable Long id) {
    return eventService.getChildrenEvents(id).stream()
      .map(factory::toPreview)
      .collect(Collectors.toList());
  }

  @GetMapping("/{id}/galleries")
  @RolesAllowed({Roles.STUDENT})
  public Page<GalleryPreview> getGalleries(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return eventService.getEventGalleries(id, page).map(galleryFactory::toPreview);
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
