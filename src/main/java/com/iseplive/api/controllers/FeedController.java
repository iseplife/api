package com.iseplive.api.controllers;


import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.services.AuthService;
import com.iseplive.api.services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/feed")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @Autowired
  AuthService authService;

  @GetMapping("/main")
  public Page<PostView> getMain(@RequestParam(defaultValue = "0") int page) {
    return feedService.getMainPosts(page);
  }

  @GetMapping("/{name}")
  public Page<PostView> getFeedPosts(@PathVariable String name, @RequestParam(defaultValue = "0") int page) {
    return feedService.getFeedPosts(name, page);
  }

  @GetMapping("/{name}/pinned")
  public List<PostView> getFeedPostsPinned(@PathVariable String name) {
    return feedService.getFeedPostsPinned(name);
  }

  @GetMapping("/{name}/waiting")
  @RolesAllowed({Roles.ADMIN})
  public List<PostView> getFeedPostsWaiting(@PathVariable String name) {
    return feedService.getFeedPostsWaiting(name);
  }

  @GetMapping("/{name}/draft")
  @RolesAllowed({Roles.STUDENT})
  public List<PostView> getClubPostDraft(@PathVariable String name) {
    return feedService.getFeedDrafts(name, authService.getLoggedUser());
  }

  @PostMapping("/{name}/subscribe")
  @RolesAllowed({Roles.STUDENT})
  public void subscribeFeed(@PathVariable String name, @AuthenticationPrincipal TokenPayload auth){
    feedService.subscribeFeed(name, auth.getId());
  }
}
