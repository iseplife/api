package com.iseplive.api.controllers.media;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.media.PollCreationDTO;
import com.iseplive.api.entity.media.poll.Poll;
import com.iseplive.api.entity.media.poll.PollVote;
import com.iseplive.api.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@RestController
@RequestMapping("/poll")
public class PollController {

  @Autowired
  PollService pollService;

  @GetMapping("/{id}")
  public Poll getPoll(@PathVariable Long id) {
    return pollService.getPoll(id);
  }

  /**
   * Check if poll has been answered
   *
   * @param id
   * @return
   */
  @GetMapping("/{id}/vote")
  @RolesAllowed({Roles.STUDENT})
  public List<PollVote> getVote(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) {
    return pollService.getVote(id, auth.getId());
  }

  @GetMapping("/{id}/vote/all")
  public List<PollVote> getAllVotes(@PathVariable Long id, @AuthenticationPrincipal TokenPayload auth) { //TODO
    return pollService.getUserVotes(id);
  }

  @PutMapping("/{id}/answer/{answerId}") // add student
  @RolesAllowed({Roles.STUDENT})
  public void vote(@PathVariable Long id, @PathVariable Long answerId, @AuthenticationPrincipal TokenPayload auth) {
    pollService.addVote(id, answerId, auth.getId());
  }

  @DeleteMapping("/{id}/answer/{answerId}") // remove student
  @RolesAllowed({Roles.STUDENT})
  public void unvote(@PathVariable Long id, @PathVariable Long answerId, @AuthenticationPrincipal TokenPayload auth) {
    pollService.removeVote(id, answerId, auth);
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Poll createPoll(@RequestParam("post") Long postId, @RequestBody PollCreationDTO dto) {
    return pollService.createPoll(postId, dto);
  }
}
