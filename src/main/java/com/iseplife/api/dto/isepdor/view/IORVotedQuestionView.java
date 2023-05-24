package com.iseplife.api.dto.isepdor.view;

import java.util.List;

import com.iseplife.api.dto.view.SearchItemView;

import lombok.Data;

@Data
public class IORVotedQuestionView {
  IORQuestionView question;
  SearchItemView vote;
  
  List<SearchItemView> choices;
}
