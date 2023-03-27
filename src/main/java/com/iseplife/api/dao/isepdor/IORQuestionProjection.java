package com.iseplife.api.dao.isepdor;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.subscription.Subscribable;

public interface IORQuestionProjection {
  @Value("#{target.question}")
  IORQuestion getQuestion();

  @Value("#{target.vote?.vote}")
  Subscribable getVote();
}
