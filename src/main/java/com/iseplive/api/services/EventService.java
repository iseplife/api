package com.iseplive.api.services;

import com.iseplive.api.constants.PublishStateEnum;
import com.iseplive.api.dao.event.EventFactory;
import com.iseplive.api.dao.event.EventRepository;
import com.iseplive.api.dto.media.EventDTO;
import com.iseplive.api.entity.Feed;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.Event;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Service
public class EventService {
  @Autowired
  EventRepository eventRepository;

  @Autowired
  EventFactory eventFactory;

  @Autowired
  ClubService clubService;

  @Autowired
  MediaUtils mediaUtils;

  @Autowired
  PostService postService;

  @Value("${storage.event.url}")
  String eventBaseUrl;

  private static final int WIDTH_EVENT_IMAGE = 1024;

  public Event createEvent(MultipartFile image, EventDTO dto) {
    Event event = eventFactory.dtoToEntity(dto);
    Club club = clubService.getClub(dto.getClubId());
    if (club == null) {
      throw new IllegalArgumentException("Could not find a club with id: " + dto.getClubId());
    }
    event.setClub(club);

    String eventPath = createImageEvent(image);

    event.setImageUrl(mediaUtils.getPublicUrlImage(eventPath));
    event = eventRepository.save(event);

    Feed eventFeed = new Feed();
    //TODO: slugify name
    eventFeed.setName(event.getTitle());
    event.setFeed(eventFeed);
    return event;
  }

  private String createImageEvent(MultipartFile image) {
    String random = mediaUtils.randomName();
    String eventPath = mediaUtils.resolvePath(
      eventBaseUrl, random, false);
    mediaUtils.saveJPG(image, WIDTH_EVENT_IMAGE, eventPath);
    return eventPath;
  }

  public List<Event> getEvents() {
    return eventRepository.findAll();
  }

  public Event getEvent(Long id) {
    Event event = eventRepository.findOne(id);
    if (event == null) {
      throw new IllegalArgumentException("could not find event with id: "+id);
    }
    return event;
  }

  public Event updateEvent(Long id, EventDTO eventDTO, MultipartFile file) {
    Event event = getEvent(id);

    event.setTitle(eventDTO.getTitle());
    event.setDescription(eventDTO.getDescription());
    event.setLocation(eventDTO.getLocation());
    event.setStartsAt(eventDTO.getDate());

    if (file != null) {
      String eventPath = createImageEvent(file);
      mediaUtils.removeIfExistPublic(event.getImageUrl());
      event.setImageUrl(mediaUtils.getPublicUrlImage(eventPath));
    }

    return eventRepository.save(event);
  }

  public void removeEvent(Long id) {
    Event event = getEvent(id);
    mediaUtils.removeIfExistPublic(event.getImageUrl());
    eventRepository.delete(id);
  }
}
