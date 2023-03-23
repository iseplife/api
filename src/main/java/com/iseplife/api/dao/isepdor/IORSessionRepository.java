package com.iseplife.api.dao.isepdor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.isepdor.IORSession;

@Repository
public interface IORSessionRepository extends CrudRepository<IORSession, Long> {
  @Query("select s from IORSession s where s.start <= now() and s.end >= now()")
  IORSession findOngoingSession();
}

