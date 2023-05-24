package com.iseplife.api.dao.isepdor;

import org.springframework.beans.factory.annotation.Value;

public interface IOROptionProjection {
  @Value("#{target.votes}")
  int getVotes();
  @Value("#{target.vote_id}")
  Long getVoteId();
  @Value("#{target.vote_type}")
  String getVoteType();
}
