package com.iseplife.api.dto.embed;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
public class PollCreationDTO {
  private String title;
  private List<String> answers;
  private Date endDate;
  private Boolean isMultiAnswers;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getAnswers() {
    return answers;
  }

  public void setAnswers(List<String> answers) {
    this.answers = answers;
  }

  public Boolean getMultiAnswers() {
    return isMultiAnswers;
  }

  public void setMultiAnswers(Boolean multiAnswers) {
    isMultiAnswers = multiAnswers;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
