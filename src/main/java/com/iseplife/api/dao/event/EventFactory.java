package com.iseplife.api.dao.event;

import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.feed.FeedFactory;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.services.SecurityService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class EventFactory {

  static public EventPreview toPreview(Event event) {
    EventPreview preview = new EventPreview();
    preview.setId(event.getId());
    preview.setTitle(event.getTitle());
    preview.setType(event.getType().name());
    preview.setTargets(event.getTargets().stream().map(Feed::getId).collect(Collectors.toSet()));
    preview.setStartsAt(event.getStartsAt());
    preview.setEndsAt(event.getEndsAt());
    preview.setCover(event.getCover());
    preview.setPublished(event.getPublishedAt().before(new Date()));
    return preview;
  }

  static public EventView toView(Event event, Boolean isSubscribed) {
    EventView view = new EventView();
    view.setId(event.getId());
    view.setType(event.getType().name());
    view.setTitle(event.getTitle());
    view.setDescription(event.getDescription());
    view.setCover(event.getCover());

    view.setStartsAt(event.getStartsAt());
    view.setEndsAt(event.getEndsAt());
    view.setLocation(event.getLocation());

    // Split string containing long & lag and parsing it into float
    if (event.getCoordinates() != null)
      view.setCoordinates(Arrays.stream(event.getCoordinates().split(";")).map(Float::valueOf).toArray(Float[]::new));

    view.setTicketURL(event.getTicketUrl());
    view.setPrice(event.getPrice());
    view.setPublished(event.getPublishedAt());
    view.setClosed(event.isClosed());

    view.setSubscribed(isSubscribed);
    view.setFeed(event.getFeed().getId());
    view.setTargets(event.getTargets().stream().map(FeedFactory::toView).collect(Collectors.toSet()));
    view.setHasRight(SecurityService.hasRightOn(event));
    view.setClub(ClubFactory.toPreview(event.getClub()));
    return view;
  }

}
