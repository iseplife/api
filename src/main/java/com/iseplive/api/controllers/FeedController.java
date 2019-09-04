package com.iseplive.api.controllers;


import com.iseplive.api.entity.post.Post;
import com.iseplive.api.services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/feed")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @GetMapping("/main")
  public List<Post> getMain() {
    return feedService.getMainPosts();
  }

  @GetMapping("/{name}")
  public List<Post> getClubPost(@PathVariable String name) {
    return feedService.getFeedPosts(name);
  }

  @GetMapping("/{name}/pinned")
  public List<Post> getClubPostPinned(@PathVariable String name) {
    return feedService.getFeedPostsPinned(name);
  }


}
