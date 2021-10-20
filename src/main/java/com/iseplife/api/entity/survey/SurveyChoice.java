package com.iseplife.api.entity.survey;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SurveyChoice {

  @Id
  @GeneratedValue
  private Long id;

  private Integer count = 0;



}
