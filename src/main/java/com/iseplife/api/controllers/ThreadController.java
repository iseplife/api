package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.thread.CommentDTO;
import com.iseplife.api.dto.thread.CommentEditDTO;
import com.iseplife.api.dto.thread.view.CommentFormView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/thread")
public class ThreadController {

  @Autowired
  ThreadService threadService;

  @GetMapping("/{id}/likes")
  @RolesAllowed({Roles.STUDENT})
  public List<Like> getLikes(@PathVariable Long id) {
    return threadService.getLikes(id);
  }

  @PutMapping("/{id}/like")
  @RolesAllowed({Roles.STUDENT})
  public Boolean toggleLike(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    return threadService.toggleLike(id, auth.getId());
  }

  @GetMapping("/{id}/comment")
  @RolesAllowed({Roles.STUDENT})
  public List<CommentView> getComments(@PathVariable Long id) {
    return threadService.getComments(id);
  }

  @PutMapping("/{id}/comment")
  @RolesAllowed({Roles.STUDENT})
  public CommentFormView commentThread(@PathVariable Long id, @RequestBody CommentDTO dto, @AuthenticationPrincipal TokenPayload auth) {
    return threadService.comment(id, dto, auth.getId());
  }

  @PutMapping("/{id}/comment/{comID}")
  @RolesAllowed({Roles.STUDENT})
  public CommentFormView editComment(@PathVariable Long id, @PathVariable Long comID, @RequestBody CommentEditDTO dto) {
    return threadService.editComment(id, comID, dto);
  }

  @DeleteMapping("/{id}/comment/{comID}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteComment(@PathVariable Long comID, @AuthenticationPrincipal TokenPayload auth) {
    threadService.deleteComment(comID, auth);
  }
}
