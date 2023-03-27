package com.iseplife.api.dao.isepdor;

import com.iseplife.api.entity.subscription.Subscribable;

public interface IOROptionProjection {
  Subscribable getVote();
  int getVotes();
}
