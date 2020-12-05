package com.iseplife.api.controllers.embed;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.embed.PollCreationDTO;
import com.iseplife.api.dto.embed.view.PollChoiceView;
import com.iseplife.api.dto.embed.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
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
    return pollService.getPollView(id);
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public PollView createPoll(@RequestBody PollCreationDTO dto) {
    return pollService.createPoll(dto);
  }

  @PostMapping("/{id}/choice/{choiceId}")
  @RolesAllowed({Roles.STUDENT})
  public void vote(@PathVariable Long id, @PathVariable Long choiceId, @AuthenticationPrincipal TokenPayload auth) {
    pollService.addVote(id, choiceId, auth.getId());
  }

  @DeleteMapping("/{id}/choice/{choiceId}")
  @RolesAllowed({Roles.STUDENT})
  public void unvote(@PathVariable Long id, @PathVariable Long choiceId, @AuthenticationPrincipal TokenPayload auth) {
    pollService.removeVote(id, choiceId, auth.getId());
  }

  @GetMapping("/{id}/vote")
  public List<PollChoiceView> getPollVotes(@PathVariable Long id) {
    return pollService.getPollVotes(id);
  }

}
