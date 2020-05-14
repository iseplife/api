package com.iseplife.api.controllers;


import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dto.group.groupDTO;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.AuthService;
import com.iseplife.api.services.FeedService;
import com.iseplife.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/feed")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @Autowired
  AuthService authService;

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostView> getFeedPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return feedService.getFeedPosts(id, page);
  }

  @GetMapping("/{id}/post/pinned")
  @RolesAllowed({Roles.STUDENT})
  public List<PostView> getFeedPostsPinned(@PathVariable Long id) {
    return feedService.getFeedPostsPinned(id);
  }

  @GetMapping("/{id}/post/waiting")
  @RolesAllowed({Roles.ADMIN})
  public List<PostView> getFeedPostsWaiting(@PathVariable Long id) {
    return feedService.getFeedPostsWaiting(id);
  }

  @GetMapping("/{id}/post/draft")
  @RolesAllowed({Roles.STUDENT})
  public List<PostView> getClubPostDraft(@PathVariable Long id) {
    return feedService.getFeedDrafts(id, authService.getLoggedUser());
  }

  @GetMapping("/{id}/subscribe")
  @RolesAllowed({Roles.STUDENT})
  public Boolean isFeedSubscribed(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth){
    return feedService.isSubscribed(id, auth.getId());
  }

  @PostMapping("/{id}/subscribe")
  @RolesAllowed({Roles.STUDENT})
  public void toggleFeedSubscription(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth){
    feedService.toggleSubscription(id, auth.getId());
  }
}
