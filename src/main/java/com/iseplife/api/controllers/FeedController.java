package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.FeedProjection;
import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.FeedService;
import com.iseplife.api.services.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
  final private FeedService feedService;
  final private ThreadService threadService;
  final private PostFactory factory;

  @GetMapping
  @RolesAllowed({Roles.STUDENT})
  public Iterable<FeedProjection> getUserFeeds(@AuthenticationPrincipal TokenPayload token){
    return feedService.getUserFeeds(token);
  }

  @GetMapping("/main/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getMainFeedPosts(@RequestParam(defaultValue = "0") int page){
    return feedService.getMainFeedPosts(page).map(p -> factory.toView(p, threadService.isLiked(p)));
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getFeedPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return feedService.getFeedPosts(id, page).map(p -> factory.toView(p, threadService.isLiked(p)));
  }

  @GetMapping("/{id}/post/pinned")
  @RolesAllowed({Roles.STUDENT})
  public List<PostProjection> getFeedPostsPinned(@PathVariable Long id) {
    return feedService.getFeedPostsPinned(id).stream()
      .map(p -> factory.toView(p, threadService.isLiked(p)))
      .collect(Collectors.toList());
  }

  @GetMapping("/{id}/post/draft")
  @RolesAllowed({Roles.STUDENT})
  public PostProjection getClubPostDraft(@PathVariable Long id) {
    return factory.toView(feedService.getFeedDrafts(id, SecurityService.getLoggedId()), false);
  }

  @GetMapping("/{id}/subscribe")
  @RolesAllowed({Roles.STUDENT})
  public Boolean isFeedSubscribed(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth){
    return feedService.isSubscribedToFeed(id, auth.getId());
  }

  @PostMapping("/{id}/subscribe")
  @RolesAllowed({Roles.STUDENT})
  public Boolean toggleFeedSubscription(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth){
    return feedService.toggleSubscription(id, auth.getId());
  }
}
