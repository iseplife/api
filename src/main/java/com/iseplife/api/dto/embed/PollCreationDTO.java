package com.iseplife.api.dto.embed;

import java.util.Date;
import java.util.List;

public class PollCreationDTO {
  private String title;
  private List<String> choices;
  private Date endsAt;
  private Boolean multiple;
  private Boolean anonymous;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
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
}
