package com.iseplife.api.entity.post.embed.poll;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.media.Embedable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Poll implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private Date endDate;
  private Boolean isMultiAnswers;

  @OneToMany(mappedBy = EmbedType.POLL, cascade = CascadeType.ALL)
  private List<PollAnswer> answers;

  private Date creation;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Date getCreation() {
    return creation;
  }

  public String getEmbedType(){
    return EmbedType.POLL;
  }
}
