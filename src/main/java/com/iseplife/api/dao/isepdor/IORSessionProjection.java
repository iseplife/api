package com.iseplife.api.dao.isepdor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.entity.isepdor.IORSession;
import com.iseplife.api.entity.isepdor.IORVote;

public interface IORSessionProjection {
  @Value("#{target.session}")
  IORSession getSession();

  @Value("#{target.vote}")
  List<IORVote> getVotes();
}
