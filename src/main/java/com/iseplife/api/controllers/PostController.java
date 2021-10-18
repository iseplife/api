package com.iseplife.api.controllers;

import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.dto.post.PostCreationDTO;
import com.iseplife.api.dto.post.PostUpdateDTO;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.view.*;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.PostService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
  final private PostService postService;
  final private PostFactory factory;

  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public PostFormView createPost(@RequestBody PostCreationDTO dto) {
    return factory.toFormView(postService.createPost(dto));
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public PostFormView updatePost(@PathVariable Long id, @RequestBody PostUpdateDTO update) {
    return factory.toFormView(postService.updatePost(id, update));
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void deletePost(@PathVariable Long id) {
    postService.deletePost(id);
  }

  @PutMapping("/{id}/pin")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void pinPost(@PathVariable Long id,  @RequestParam(name="pinned", defaultValue = "0", required = false) Boolean pinned) {
    postService.updatePostPinnedStatus(id, pinned);
  }

  @PutMapping("/{id}/homepage-forced")
  @RolesAllowed({Roles.ADMIN})
  public void updateForcedHomepage(@PathVariable Long id, @RequestParam(name="enable", defaultValue = "0", required = false) Boolean enable) {
    postService.updateForceHomepageStatus(id, enable);
  }

  @GetMapping("/authors")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Set<AuthorView> getAuthors(@RequestParam(name = "club") Boolean clubOnly) {
    return postService.getAuthorizedPublish(SecurityService.getLoggedId(), clubOnly);
  }
}
