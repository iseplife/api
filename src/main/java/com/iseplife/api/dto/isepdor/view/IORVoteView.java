package com.iseplife.api.dto.isepdor.view;

import com.iseplife.api.entity.isepdor.IORQuestion;

import lombok.Data;

@Data
public class IORVoteView {
  IORQuestion question;
  Object vote;
}
