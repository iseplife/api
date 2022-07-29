package com.iseplife.api.entity.post.embed.poll;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter @Setter @NoArgsConstructor
public class PollChoice {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String content;

  @ManyToOne
  private Poll poll;

  @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PollVote> votes = new ArrayList<>();

  public int getVotesNb() {
    return votes.size();
  }

  public List<Long> getVoters() {
    return votes.stream()
      .map(v -> v.getStudent().getId())
      .collect(Collectors.toList());
  }
}
