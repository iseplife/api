package com.iseplive.api.controllers.media;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.media.EventDTO;
import com.iseplive.api.entity.media.Event;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.services.EventService;
import com.iseplive.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
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

  @Autowired
  JsonUtils jsonUtils;

  private boolean hasRights(TokenPayload payload, Long clubId) {
    if (!payload.getRoles().contains(Roles.ADMIN) && !payload.getRoles().contains(Roles.EVENT_MANAGER)) {
      // if user is not the club admin
      return !payload.getClubsAdmin().contains(clubId);
    }
    return false;
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.EVENT_MANAGER, Roles.STUDENT})
  public Event createEvent(@RequestParam("post") Long postId,
                           @RequestParam("image") MultipartFile file,
                           @RequestParam("event") String event,
                           @AuthenticationPrincipal TokenPayload auth) {
    EventDTO eventDTO = jsonUtils.deserialize(event, EventDTO.class);
    if (hasRights(auth, eventDTO.getClubId())) {
      throw new AuthException("you are not this club's admin");
    }
    return eventService.createEvent(postId, file, eventDTO);
  }

  @GetMapping
  public List<Event> getEvents() {
    return eventService.getEvents();
  }

  @GetMapping("/{id}")
  public Event getEvent(@PathVariable Long id) {
    return eventService.getEvent(id);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.EVENT_MANAGER, Roles.STUDENT})
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
  @RolesAllowed({Roles.ADMIN, Roles.EVENT_MANAGER})
  public void deleteEvent(@PathVariable Long id) {
    eventService.removeEvent(id);
  }

}
