package com.iseplife.api.dto.poll.view;

import lombok.Data;

@Data
public class PollChoiceView {
  private Long id;
  private String content;
  private Integer votesNumber;
  private boolean voted;
}
