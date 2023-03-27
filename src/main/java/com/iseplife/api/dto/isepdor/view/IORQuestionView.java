package com.iseplife.api.dto.isepdor.view;

import com.iseplife.api.entity.isepdor.IORQuestionType;

import lombok.Data;

@Data
public class IORQuestionView {
  private Long id;

  private int position;

  private String title;
  
  private Integer promo;

  private IORQuestionType type;
}
