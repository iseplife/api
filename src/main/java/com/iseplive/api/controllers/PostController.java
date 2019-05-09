package com.iseplive.api.controllers;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.PublishStateEnum;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.CommentDTO;
import com.iseplive.api.dto.PostDTO;
import com.iseplive.api.dto.PostUpdateDTO;
import com.iseplive.api.dto.view.CommentView;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.Comment;
import com.iseplive.api.entity.Post;
import com.iseplive.api.entity.user.Author;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.services.AuthService;
import com.iseplive.api.services.PostService;
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
  AuthService authService;

  @GetMapping("/waiting")
  @RolesAllowed({ Roles.STUDENT })
  public List<PostView> getWaitingPosts(@AuthenticationPrincipal TokenPayload token) {
    return postService.getWaitingPosts(token);
  }

  @GetMapping
  public Page<PostView> getPosts(@RequestParam(defaultValue = "0") int page) {
    if (authService.isUserAnonymous()) {
      return postService.getPublicPosts(page);
    }
    return postService.getPosts(page);
  }

  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public Post createPost(@RequestBody PostDTO post, @AuthenticationPrincipal TokenPayload auth) {

    return postService.createPost(auth, post);
  }

  @GetMapping("/pinned")
  public List<PostView> getPinnedPosts() {
    if (authService.isUserAnonymous()) {
      return postService.getPublicPinnedPosts();
    }
    return postService.getPinnedPosts();
  }

  @GetMapping("/authors")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public List<Author> getAuthors(@AuthenticationPrincipal TokenPayload auth) {
    return postService.getAuthors(auth);
  }

  @GetMapping("/comment/{id}/likes")
  public Set<Student> getLikesComment(@PathVariable Long id) {
    return postService.getLikesComment(id);
  }

  @GetMapping("/{id}")
  public PostView getPost(@PathVariable Long id) {
    return postService.getPostView(id);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Post updatePost(@PathVariable Long id,
                         @RequestBody PostUpdateDTO update,
                         @AuthenticationPrincipal TokenPayload auth) {
    return postService.updatePost(id, update, auth);
  }

  @PutMapping("/{id}/pinned/{pinned}")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public void pinPost(@PathVariable Long id,
                      @PathVariable Boolean pinned,
                      @AuthenticationPrincipal TokenPayload auth) {
    postService.setPinnedPost(id, pinned, auth);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public void deletePost(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    postService.deletePost(id, auth);
  }

  @GetMapping("/{id}/comment")
  public List<CommentView> getComments(@PathVariable Long id) {
    return postService.getComments(id);
  }

  @PutMapping("/{id}/comment")
  @RolesAllowed({Roles.STUDENT})
  public Comment commentPost(@PathVariable Long id, @RequestBody CommentDTO dto, @AuthenticationPrincipal TokenPayload auth) {
    return postService.commentPost(id, dto, auth.getId());
  }

  @PutMapping("/{id}/like")
  @RolesAllowed({Roles.STUDENT})
  public void likePost(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    postService.togglePostLike(id, auth.getId());
  }

  @GetMapping("/{id}/likes")
  public Set<Student> getLikesPost(@PathVariable Long id) {
    return postService.getLikesPost(id);
  }

  @DeleteMapping("/{id}/comment/{comId}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteComment(@PathVariable Long comId, @AuthenticationPrincipal TokenPayload auth) {
    postService.deleteComment(comId, auth.getId());
  }

  @PutMapping("/{id}/comment/{comId}/like")
  @RolesAllowed({Roles.STUDENT})
  public void toggleCommentLike(@PathVariable Long comId, @AuthenticationPrincipal TokenPayload auth) {
    postService.toggleCommentLike(comId, auth.getId());
  }

  @PutMapping("/{id}/state/{state}")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public void setPublishState(@PathVariable("id") Long id, @PathVariable("state") PublishStateEnum state) {
    postService.setPublishState(id, state);
  }

  @PutMapping("/{id}/embed/{media}")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public void addMediaEmbed(@PathVariable Long id, @PathVariable Long media) {
    postService.addMediaEmbed(id, media);
  }

}
