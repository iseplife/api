package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.CommentDTO;
import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.*;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.CommentDTO;
import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.services.AuthService;
import com.iseplife.api.services.PostService;
import com.iseplife.api.services.StudentService;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@RestController
@RequestMapping("/post")
public class PostController {

  @Autowired
  PostService postService;

  @Autowired
  ThreadService threadService;

  @Autowired
  StudentService studentService;

  @Autowired
  AuthService authService;


  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public Post createPost(@RequestBody PostDTO post, @AuthenticationPrincipal TokenPayload auth) {
    return postService.createPost(auth, post);
  }

  @GetMapping("/{id}")
  public PostView getPost(@PathVariable Long id) {
    return postService.getPostView(id);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Post updatePost(@PathVariable Long id,
                         @RequestBody PostUpdateDTO update,
                         @AuthenticationPrincipal TokenPayload auth) {
    return postService.updatePost(id, update, auth);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void deletePost(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    postService.deletePost(id, auth);
  }

  @PutMapping("/{id}/pinned/{pinned}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void pinPost(@PathVariable Long id,
                      @PathVariable Boolean pinned,
                      @AuthenticationPrincipal TokenPayload auth) {
    postService.setPinnedPost(id, pinned, auth);
  }

  @GetMapping("/authors")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Set<AuthorView> getAuthors(@AuthenticationPrincipal TokenPayload auth) {
    return postService.getAuthorizedPublish(auth);
  }


  @PutMapping("/{id}/state/{state}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void setPublishState(@PathVariable("id") Long id, @PathVariable("state") PublishStateEnum state) {
    postService.setPublishState(id, state);
  }

  @PutMapping("/{id}/embed/{media}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void addMediaEmbed(@PathVariable Long id, @PathVariable Long media) {
    postService.addMediaEmbed(id, media);
  }



  /**
   *  @deprecated Use feed's controller to get waiting posts
   */
  @GetMapping("/waiting")
  @RolesAllowed({ Roles.STUDENT })
  public List<PostView> getWaitingPosts(@AuthenticationPrincipal TokenPayload token) {
    return postService.getWaitingPosts(token);
  }

  /**
   * @deprecated Use feed's controller to get posts
   */
  @GetMapping
  public Page<PostView> getPosts(@RequestParam(defaultValue = "0") int page) {
    return authService.isUserAnonymous() ? postService.getPublicPosts(page) : postService.getPosts(page);
  }

  /**
   * @deprecated Use feed's controller to get pinned posts
   */
  @GetMapping("/pinned")
  public List<PostView> getPinnedPosts() {
    return authService.isUserAnonymous() ? postService.getPublicPinnedPosts() : postService.getPinnedPosts();
  }
}
