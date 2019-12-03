package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.*;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.services.AuthService;
import com.iseplife.api.services.PostService;
import com.iseplife.api.services.StudentService;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;


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

  @PutMapping("/{id}/pin")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void togglePinPost(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    postService.togglePinnedPost(id, auth);
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

  @PutMapping("/{id}/embed/{embedType}/{embedID}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void addMediaEmbed(@PathVariable Long id, @PathVariable String embedType, @PathVariable Long embedID) {
    postService.addMediaEmbed(id, embedType, embedID);
  }

}
