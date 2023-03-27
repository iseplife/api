package com.iseplife.api.entity.isepdor;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class IORQuestion {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private int position;

  private String title;
  
  private Integer promo;

  private IORQuestionType type;

  @ManyToOne(fetch = FetchType.LAZY)
  private IORSession session;
  
  @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
  private List<IORVote> votes;
  
}
