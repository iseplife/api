package com.iseplife.api.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.event.EventDTO;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.media.view.MediaNameView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.services.EventService;
import com.iseplife.api.services.SubscriptionService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
  final private SubscriptionService subscriptionService;
  final private EventService eventService;
  final private EventFactory factory;
  final private GalleryFactory galleryFactory;

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public EventView createEvent(@RequestBody EventDTO dto) {
    Event event = eventService.createEvent(dto);
    return factory.toView(event, subscriptionService.isSubscribed(event));
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
    return factory.toView(event, subscriptionService.isSubscribed(event));
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
