package com.iseplife.api.entity.post.embed.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
public class PollChoice {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String content;

  @ManyToOne
  private Poll poll;

  @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
  private List<PollVote> votes = new ArrayList<>();

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

  public List<String> getVoters() {
    return votes.stream()
      .map(v -> v.getStudent().getFirstName() + " " + v.getStudent().getLastName())
      .collect(Collectors.toList());
  }

  public void setVotes(List<PollVote> votes) {
    this.votes = votes;
  }
}
