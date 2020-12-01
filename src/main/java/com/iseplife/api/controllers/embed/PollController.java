package com.iseplife.api.controllers.embed;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.embed.PollCreationDTO;
import com.iseplife.api.dto.embed.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/poll")
public class PollController {

  @Autowired
  PollService pollService;

  @GetMapping("/{id}")
  public PollView getPoll(@PathVariable Long id) {
    return PollFactory.toView(pollService.getPoll(id));
  }

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
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public PollView createPoll(@RequestBody PollCreationDTO dto) {
    return pollService.createPoll(dto);
  }
}
