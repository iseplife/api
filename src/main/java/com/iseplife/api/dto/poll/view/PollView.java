package com.iseplife.api.dto.poll.view;

import com.iseplife.api.dto.view.EmbedView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PollView extends EmbedView {
  private Long id;
  private String title;
  private Date endsAt;
  private List<PollChoiceView> choices;
  private Boolean multiple;
  private Boolean anonymous;
  private Boolean hasVoted;
}
