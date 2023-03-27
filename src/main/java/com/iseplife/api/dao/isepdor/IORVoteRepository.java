package com.iseplife.api.dao.isepdor;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.isepdor.IORVote;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.user.Student;

@Repository
public interface IORVoteRepository extends CrudRepository<IORVote, Long> {
  Optional<IORVote> findOneByQuestionAndVoter(IORQuestion question, Student voter);
  
  @Transactional
  @Modifying
  @Query("update IORVote v set v.vote = :voted where v.question = :question and v.voter = :student")
  void updateVote(Student student, IORQuestion question, Subscribable voted);
}
