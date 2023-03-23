package com.iseplife.api.dao.isepdor;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.isepdor.IORVote;
import com.iseplife.api.entity.user.Student;

@Repository
public interface IORVoteRepository extends CrudRepository<IORVote, Long> {
  Optional<IORVote> findOneByQuestionAndVoter(IORQuestion question, Student voter);
}

