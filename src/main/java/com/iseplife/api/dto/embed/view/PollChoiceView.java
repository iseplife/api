package com.iseplife.api.dto.embed.view;

import java.util.List;

public class PollChoiceView {
  private Long id;
  private String content;
  private Integer votesNumber;
  private List<String> voters;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getVotesNumber() {
    return votesNumber;
  }

  public void setVotesNumber(Integer votesNumber) {
    this.votesNumber = votesNumber;
  }

  public List<String> getVoters() {
    return voters;
  }

  public void setVoters(List<String> voters) {
    this.voters = voters;
  }

}
