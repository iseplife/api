package com.iseplive.api.entity.media.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
public class PollAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String content;

  @ManyToOne
  private Poll poll;

  @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
  private List<PollVote> votes;

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

  @JsonIgnore
  public Poll getPoll() {
    return poll;
  }

  public void setPoll(Poll poll) {
    this.poll = poll;
  }

  public int getVotesNb() {
    return votes.size();
  }

  public List<Long> getVoters() {
    return votes.stream()
      .map(v -> v.getStudent().getId())
      .collect(Collectors.toList());
  }

  public void setVotes(List<PollVote> votes) {
    this.votes = votes;
  }
}
