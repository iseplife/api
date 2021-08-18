package com.iseplife.api.dto.embed;

import java.util.Date;
import java.util.List;

public class PollEditionDTO {
  private Long id;
  private String title;
  private List<PollChoiceDTO> choices;
  private Date endsAt;
  private Boolean multiple;
  private Boolean anonymous;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<PollChoiceDTO> getChoices() {
    return choices;
  }

  public void setChoices(List<PollChoiceDTO> choices) {
    this.choices = choices;
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

  public Boolean getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(Boolean anonymous) {
    this.anonymous = anonymous;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
