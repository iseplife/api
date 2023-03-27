package com.iseplife.api.dto.isepdor.view;

import java.util.List;

import lombok.Data;

@Data
public class IORVotedQuestionView {
  IORQuestionView question;
  Object vote;
  
  List<Object> choices;
}
