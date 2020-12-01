package com.iseplife.api.dao.poll;

import com.iseplife.api.dto.embed.view.PollChoiceView;
import com.iseplife.api.dto.embed.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PollFactory {

  static public PollView toView(Poll poll) {
    PollView view = new PollView();

    view.setId(poll.getId());
    view.setTitle(poll.getTitle());

    view.setEndsAt(poll.getEndsAt());
    view.setMultiple(poll.getMultiple());
    view.setAnonymous(poll.getAnonymous());


    List<PollChoiceView> choices = new ArrayList<>();
    poll.getChoices().forEach(option -> {
      PollChoiceView choiceView = new PollChoiceView();

      choiceView.setId(option.getId());
      choiceView.setVotesNumber(option.getVotesNb());
      choiceView.setContent(option.getContent());

      if(!poll.getAnonymous())
        choiceView.setVoters(option.getVoters());

      choices.add(choiceView);
    });
    view.setChoices(choices);

    return view;
  }
}
