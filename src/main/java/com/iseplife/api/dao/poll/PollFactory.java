package com.iseplife.api.dao.poll;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dto.poll.view.PollChoiceView;
import com.iseplife.api.dto.poll.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PollFactory {
  final private ModelMapper mapper;
  final private PollRepository pollRepository;
  final private PollChoiceRepository pollChoiceRepository;
  
  public PollView toView(PollProjection poll, Long studentId) {
    PollView mapped = mapper.map(poll, PollView.class);
    if (mapped.getChoices().size() == 0 && studentId != null)
      fillChoices(mapped, studentId);
    mapped.setEmbedType(EmbedType.POLL);
    return mapped;
  }

  public void fillChoices(PollView mapped, Long studentId) {
    mapped.setChoices(pollChoiceRepository.findAllByPoll(mapped.getId(), studentId)
        .stream()
        .map(this::toView)
        .collect(Collectors.toList())
    );
  }

  public PollChoiceView toView(PollChoiceProjection pollChoiceProjection) {
    return mapper.map(pollChoiceProjection, PollChoiceView.class);
  }

  public PollView toView(Poll poll, Long studentId) {
    return toView(pollRepository.findProjectionById(poll.getId()), studentId);
  }

  public PollView toView(Poll poll) {
    return toView(pollRepository.findProjectionById(poll.getId()), null);
  }

}
