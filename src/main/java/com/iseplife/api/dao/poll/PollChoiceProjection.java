package com.iseplife.api.dao.poll;

import org.springframework.beans.factory.annotation.Value;

public interface PollChoiceProjection {
  @Value("#{target.p.id}")
  Long getId();
  @Value("#{target.p.content}")
  String getContent();
  @Value("#{target.p.votes.size}")
  int getVotesNumber();
  @Value("#{target.voted}")
  Boolean getVoted();
}
