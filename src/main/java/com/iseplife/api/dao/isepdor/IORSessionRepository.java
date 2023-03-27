package com.iseplife.api.dao.isepdor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.isepdor.IORSession;

@Repository
public interface IORSessionRepository extends CrudRepository<IORSession, Long> {
  @Query("select s, vote from IORSession s where s.start <= now() and s.end >= now() left join IORVote vote on vote.question.session = s and vote.voter.id = :studentId")
  IORSessionProjection findOngoingSessionWithVotes(Long studentId);

  @Query("select s from IORSession s where s.start <= now() and s.end >= now()")
  IORSession findOngoingSession();
}

