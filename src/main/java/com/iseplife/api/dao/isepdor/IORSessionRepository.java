package com.iseplife.api.dao.isepdor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.isepdor.IORSession;

@Repository
public interface IORSessionRepository extends CrudRepository<IORSession, Long> {
  @Query("select s, vote from IORSession s left join IORVote vote on vote.question.session = s and vote.voter.id = :studentId where s.start <= now() and s.ending >= now()")
  IORVotedSessionProjection findOngoingSessionWithVotes(Long studentId);

  @Query("select s.id as id, s.start as start, s.ending as ending from IORSession s where s.start <= now() and s.ending >= now()")
  IORSessionProjection findOngoingSession();
}

