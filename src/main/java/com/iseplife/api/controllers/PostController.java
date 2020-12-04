package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.post.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.*;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.constants.PostState;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.post.view.PostView;
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

  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public PostView createPost(@RequestBody PostDTO post, @AuthenticationPrincipal TokenPayload auth) {
    return postService.createPost(auth, post);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public PostView getPost(@PathVariable Long id) {
    return postService.getPostView(id);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Post updatePost(@PathVariable Long id,
                         @RequestBody PostUpdateDTO update) {
    return postService.updatePost(id, update);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void deletePost(@PathVariable Long id) {
    postService.deletePost(id);
  }

  @PutMapping("/{id}/pin")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void togglePinPost(@PathVariable Long id) {
    postService.togglePinnedPost(id);
  }

  @GetMapping("/authors")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Set<AuthorView> getAuthors(@RequestParam(name = "club") Boolean clubOnly, @AuthenticationPrincipal TokenPayload auth) {
    return postService.getAuthorizedPublish(auth, clubOnly);
  }

  @PutMapping("/{id}/state/{state}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void setPublishState(@PathVariable("id") Long id, @PathVariable("state") PostState state) {
    postService.setPublishState(id, state);
  }

  @PutMapping("/{id}/embed/{embedType}/{embedID}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void addMediaEmbed(@PathVariable Long id, @PathVariable String embedType, @PathVariable Long embedID) {
    postService.addMediaEmbed(id, embedType, embedID);
  }

}
