package com.iseplife.api.entity.isepdor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
}
