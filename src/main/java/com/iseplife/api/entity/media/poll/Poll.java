package com.iseplife.api.entity.media.poll;

import com.iseplife.api.constants.MediaType;
import com.iseplife.api.entity.media.Media;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.POLL)
public class Poll extends Media {

  private String name;
  private Date endDate;
  private Boolean isMultiAnswers;

  @OneToMany(mappedBy = MediaType.POLL, cascade = CascadeType.ALL)
  private List<PollAnswer> answers;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PollAnswer> getAnswers() {
    return answers;
  }

  public void setAnswers(List<PollAnswer> answers) {
    this.answers = answers;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Boolean getMultiAnswers() {
    return isMultiAnswers;
  }

  public void setMultiAnswers(Boolean multiAnswers) {
    isMultiAnswers = multiAnswers;
  }
}
