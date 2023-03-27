package com.iseplife.api.dao.isepdor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.isepdor.IORSession;
import com.iseplife.api.entity.subscription.Subscribable;

@Repository
public interface IORQuestionRepository extends CrudRepository<IORQuestion, Long> {
  @Query("select distinct " +
      "q as question, " +
      "v as vote " +
    "from IORQuestion q " +
      "left join q.votes v on v.voter.id = :loggedId " +
    "where " +
      "q.session = :session"
  )
  List<IORQuestionProjection> findQuestions(Long loggedId, IORSession session);

  @Query("select distinct " +
      "q as question, " +
      "v as vote " +
    "from IORQuestion q " +
      "left join q.votes v on v.voter.id = :loggedId " +
    "where " +
      "q.id = :questionId"
  )
  Optional<IORQuestionProjection> findQuestion(Long loggedId, Long questionId);
  
  @Query("select " +
      "v.vote as vote," +
      "count(v.id) as votes " +
    "from IORVote v " +
    "where " +
      "v.question = :question " +
    "group by v.vote, v.id"
  )
  Page<IOROptionProjection> findOptions(IORQuestion question, Pageable pageable);
}

