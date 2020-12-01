package com.iseplife.api.dto.embed.view;

import java.util.Date;
import java.util.List;

public class PollView {
  private Long id;

  private String title;
  private Date endsAt;
  private List<PollChoiceView> choices;
  private Boolean multiple;
  private Boolean anonymous;
  private Boolean hasVoted;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getEndsAt() {
    return endsAt;
  }

  public void setEndsAt(Date endsAt) {
    this.endsAt = endsAt;
  }

  public Boolean getMultiple() {
    return multiple;
  }

  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  public Boolean getHasVoted() {
    return hasVoted;
  }

  public void setHasVoted(Boolean hasVoted) {
    this.hasVoted = hasVoted;
  }

  public List<PollChoiceView> getChoices() {
    return choices;
  }

  public void setChoices(List<PollChoiceView> choices) {
    this.choices = choices;
  }

  public Boolean getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(Boolean anonymous) {
    this.anonymous = anonymous;
  }
}
