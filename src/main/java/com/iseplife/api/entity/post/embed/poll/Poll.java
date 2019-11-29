package com.iseplife.api.entity.post.embed.poll;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.media.Embed;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue(EmbedType.POLL)
public class Poll extends Embed {

  private String name;
  private Date endDate;
  private Boolean isMultiAnswers;

  @OneToMany(mappedBy = EmbedType.POLL, cascade = CascadeType.ALL)
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
