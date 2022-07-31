package com.iseplife.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.feed.FeedProjection;
import com.iseplife.api.dao.post.CommentFactory;
import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.FeedService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.ThreadService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
  final private FeedService feedService;
  final private ThreadService threadService;
  final private PostFactory factory;
  final private CommentFactory commentFactory;

  @GetMapping
  @RolesAllowed({Roles.STUDENT})
  public Iterable<FeedProjection> getUserFeeds(@AuthenticationPrincipal TokenPayload token){
    return feedService.getUserFeeds(token);
  }

  @GetMapping("/main/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getHomepageFeedPosts(@RequestParam(defaultValue = "0") int page){
    return feedService.getHomepageFeedPosts(page).map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());

      return factory.toView(
          p,
          trendingComment == null ? null : commentFactory.toView(trendingComment),
          SecurityService.getLoggedId()
      );
    });
  }

  @GetMapping("/main/prevposts")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getPreviousHomepageFeedPosts(@RequestParam(defaultValue = "0") Long lastDate){
    return feedService.getPreviousHomepageFeedPosts(lastDate).map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());

      return factory.toView(
          p,
          trendingComment == null ? null : commentFactory.toView(trendingComment),
          SecurityService.getLoggedId()
      );
    });
  }

  @GetMapping("/main/post/pinned")
  @RolesAllowed({Roles.STUDENT})
  public List<PostProjection> getHomepageFeedPostsPinned(){
    return feedService.getHomepageFeedPostsPinned().stream().map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());

      return factory.toView(
        p,
        trendingComment == null ? null : commentFactory.toView(trendingComment),
        SecurityService.getLoggedId()
      );
    }).collect(Collectors.toList());
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getFeedPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return feedService.getFeedPosts(id, page).map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());
      return factory.toView(
          p,
          trendingComment == null ? null : commentFactory.toView(trendingComment),
          SecurityService.getLoggedId()
      );
    });
  }

  @GetMapping("/{feedId}/{postId}")
  @RolesAllowed({Roles.STUDENT})
  public PostView getFeedPosts(@PathVariable Long feedId, @PathVariable Long postId) {
    PostProjection post = feedService.getFeedPost(feedId, postId);
    if(post == null || !SecurityService.hasReadAccess(post, feedService.getFeed(post.getContext().getFeedId())))    
      throw new HttpNotFoundException("post_not_found");
    CommentProjection trendingComment = threadService.getTrendingComment(post.getThread());
    return factory.toView(
        post,
        trendingComment == null ? null : commentFactory.toView(trendingComment),
        SecurityService.getLoggedId()
    );
  }

  @GetMapping("/{id}/prevposts")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getPreviousFeedPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") Long lastDate) {
    return feedService.getPreviousFeedPosts(id, lastDate).map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());
      return factory.toView(
          p,
          trendingComment == null ? null : commentFactory.toView(trendingComment),
          SecurityService.getLoggedId()
      );
    });
  }

  @GetMapping("/{id}/post/pinned")
  @RolesAllowed({Roles.STUDENT})
  public List<PostProjection> getFeedPostsPinned(@PathVariable Long id) {
    return feedService.getFeedPostsPinned(id).stream()
      .map(p -> {
        CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());
        return factory.toView(
            p,
            trendingComment == null ? null : commentFactory.toView(trendingComment),
            SecurityService.getLoggedId()
        );
      })
      .collect(Collectors.toList());
  }

  @GetMapping("/{id}/post/draft")
  @RolesAllowed({Roles.STUDENT})
  public PostProjection getClubPostDraft(@PathVariable Long id) {
    return factory.toView(feedService.getFeedDrafts(id, SecurityService.getLoggedId()), null, null);
  }
}
