package com.iseplife.api.entity.post.embed.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Embedable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Poll implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private String title;
  private Date endsAt;
  private Boolean multiple;
  private Boolean anonymous;


  @OneToMany(mappedBy="poll", cascade = CascadeType.ALL)
  private List<PollChoice> choices;

  @ManyToOne
  @JsonIgnore
  private Feed feed;

  private Date creation;

  @Override
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

  public Boolean getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(Boolean anonymous) {
    this.anonymous = anonymous;
  }

  public List<PollChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<PollChoice> answers) {
    this.choices = answers;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public String getEmbedType(){
    return EmbedType.POLL;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }
}
