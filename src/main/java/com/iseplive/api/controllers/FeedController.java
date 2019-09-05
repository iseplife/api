package com.iseplive.api.controllers;


import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/feed")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @GetMapping("/main")
  public Page<PostView> getMain(@RequestParam(defaultValue = "0") int page) {
    return feedService.getMainPosts(page);
  }

  @GetMapping("/{name}")
  public Page<PostView> getClubPost(@PathVariable String name, @RequestParam(defaultValue = "0") int page) {
    return feedService.getFeedPosts(name, page);
  }

  @GetMapping("/{name}/pinned")
  public List<PostView> getClubPostPinned(@PathVariable String name) {
    return feedService.getFeedPostsPinned(name);
  }


}
