package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.CommentDTO;
import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.entity.post.Comment;
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
  public List<Like> getLikes(@PathVariable Long id) {
    return threadService.getLikes(id);
  }

  @PutMapping("/{id}/like")
  @RolesAllowed({Roles.STUDENT})
  public void likeThread(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    threadService.toggleLike(id, auth.getId());
  }

  @GetMapping("/{id}/comment")
  public List<CommentView> getComments(@PathVariable Long id) {
    return threadService.getComments(id);
  }

  @PutMapping("/{id}/comment")
  @RolesAllowed({Roles.STUDENT})
  public Comment commentThread(@PathVariable Long id, @RequestBody CommentDTO dto, @AuthenticationPrincipal TokenPayload auth) {
    return threadService.comment(id, dto, auth.getId());
  }

  @PutMapping("/{id}/comment/{comId}/like")
  @RolesAllowed({Roles.STUDENT})
  public void toggleCommentLike(@PathVariable Long comId, @AuthenticationPrincipal TokenPayload auth) {
    threadService.toggleCommentLike(comId, auth.getId());
  }

  @DeleteMapping("/{id}/comment/{comId}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteComment(@PathVariable Long comID, @AuthenticationPrincipal TokenPayload auth) {
    threadService.deleteComment(comID, auth.getId());
  }
}
