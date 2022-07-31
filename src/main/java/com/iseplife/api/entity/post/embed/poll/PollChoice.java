package com.iseplife.api.entity.post.embed.poll;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;


@Entity
@Getter @Setter @NoArgsConstructor
public class PollChoice {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String content;

  @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<PollVote> votes = new ArrayList<>();

  @ManyToOne
  private Poll poll;
}
