package com.iseplife.api.controllers.embed;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.poll.PollCreationDTO;
import com.iseplife.api.dto.poll.PollEditionDTO;
import com.iseplife.api.dto.poll.view.PollChoiceView;
import com.iseplife.api.dto.poll.view.PollView;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.PollService;
import com.iseplife.api.services.SecurityService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/poll")
@RequiredArgsConstructor
public class PollController {
  final private PollService pollService;

  @GetMapping("/{id}")
  public PollView getPoll(@PathVariable Long id) {
    return pollService.getPollView(id, SecurityService.getLoggedId());
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public PollView createPoll(@RequestBody PollCreationDTO dto) {
    return pollService.createPoll(dto, SecurityService.getLoggedId());
  }

  @PutMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public PollView update(@RequestBody PollEditionDTO dto) { return pollService.updatePoll(dto, SecurityService.getLoggedId());}


  @PostMapping("/{id}/choice/{choiceId}")
  @RolesAllowed({Roles.STUDENT})
  public List<PollChoiceView> vote(@PathVariable Long id, @PathVariable Long choiceId, @AuthenticationPrincipal TokenPayload auth) {
    return pollService.addVote(id, choiceId, auth.getId());
  }

  @DeleteMapping("/{id}/choice/{choiceId}")
  @RolesAllowed({Roles.STUDENT})
  public List<PollChoiceView> unvote(@PathVariable Long id, @PathVariable Long choiceId, @AuthenticationPrincipal TokenPayload auth) {
    return pollService.removeVote(id, choiceId, auth.getId());
  }

  @GetMapping("/{id}/vote")
  public List<PollChoiceView> getPollVotes(@PathVariable Long id) {
    return pollService.getPollVotes(id, SecurityService.getLoggedId());
  }

}
