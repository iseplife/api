package com.iseplife.api.dao.poll;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dto.poll.view.PollChoiceView;
import com.iseplife.api.dto.poll.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
import com.iseplife.api.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PollFactory {
  final private ModelMapper mapper;

  static public PollView toView(Poll poll) {
    PollView view = new PollView();

    view.setId(poll.getId());
    view.setTitle(poll.getTitle());

    view.setEndsAt(poll.getEndsAt());
    view.setMultiple(poll.isMultiple());
    view.setAnonymous(poll.isAnonymous());


    List<PollChoiceView> choices = new ArrayList<>();
    poll.getChoices().forEach(option -> {
      choices.add(poll.isAnonymous() ? toAnonymousView(option) : toView(option));
    });
    view.setChoices(choices);
    view.setEmbedType(EmbedType.POLL);

    return view;
  }

  static public PollChoiceView toView(PollChoice choice) {
    PollChoiceView choiceView = new PollChoiceView();

    choiceView.setId(choice.getId());
    choiceView.setVotesNumber(choice.getVotesNb());
    choiceView.setContent(choice.getContent());

    choiceView.setVoters(choice.getVoters());

    return choiceView;
  }

  static public PollChoiceView toAnonymousView(PollChoice choice) {
    PollChoiceView anonymousChoiceView = new PollChoiceView();

    anonymousChoiceView.setId(choice.getId());
    anonymousChoiceView.setVotesNumber(choice.getVotesNb());
    anonymousChoiceView.setContent(choice.getContent());

    anonymousChoiceView.setVoters(choice.getVoters());
    if (choice.getVoters().contains(SecurityService.getLoggedId())) {
      anonymousChoiceView.setVoters(Collections.singletonList(SecurityService.getLoggedId()));
    }
    return anonymousChoiceView;
  }

}
