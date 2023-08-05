package com.iseplife.api.dao.event;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.iseplife.api.entity.post.embed.Gallery;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.dto.event.view.EventTabPreview;
import com.iseplife.api.dto.event.view.EventView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.services.SecurityService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactory {
  @Lazy
  final private ClubFactory clubFactory;
  final private ModelMapper mapper;

  @SuppressWarnings("unchecked")
  @PostConstruct()
  public void init() {
    mapper.typeMap(Event.class, EventPreview.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ((Set<Feed>) ctx.getSource()).stream().map(Feed::getId).collect(Collectors.toSet()))
          .map(Event::getTargets, EventPreview::setTargets);
        mapper
          .using(ctx -> ((Date) ctx.getSource()).before(new Date()))
          .map(Event::getPublishedAt, EventPreview::setPublished);
      });

    mapper.typeMap(Event.class, EventView.class)
      .addMappings(mapper -> {
        mapper.map(src -> src.getFeed().getId(), EventView::setFeed);
        mapper
          .using(ctx -> ((Set<Feed>) ctx.getSource()).stream().map(Feed::getId).collect(Collectors.toSet()))
          .map(Event::getTargets, EventView::setTargets);
        mapper
          .using(ctx -> ctx.getSource() != null ?
            Arrays.stream(((String) ctx.getSource()).split(";")).map(Float::valueOf).toArray(Float[]::new) :
            null
          );
        mapper
          .using(ctx -> SecurityService.hasRightOn((Event) ctx.getSource()))
          .map(src -> src, EventView::setHasRight);
        mapper
          .using(ctx -> SecurityService.hasGalleryClubsAccessOn((Event) ctx.getSource()))
          .map(src -> src, EventView::setClubsAllowedToPublishGallery);
        mapper
          .using(ctx -> clubFactory.toPreview((Club) ctx.getSource()))
          .map(Event::getClub, EventView::setClub);
      });
  }

  public EventPreview toPreview(Event event) {
    return mapper.map(event, EventPreview.class);
  }

  public EventTabPreview toTabPreview(EventTabPreviewProjection event) {
    return mapper.map(event, EventTabPreview.class);
  }

  public EventView toView(Event event, SubscriptionProjection subProjection) {
    EventView view = mapper.map(event, EventView.class);
    view.setSubscribed(subProjection);

    return view;
  }

}
