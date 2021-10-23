package com.iseplife.api.dto.embed.view;

import lombok.Data;

import java.util.List;

@Data
public class PollChoiceView {
  private Long id;
  private String content;
  private Integer votesNumber;
  private List<Long> voters;
}
