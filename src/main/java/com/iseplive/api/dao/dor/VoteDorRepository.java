package com.iseplive.api.dao.dor;

import com.iseplive.api.entity.dor.QuestionDor;
import com.iseplive.api.entity.dor.SessionDor;
import com.iseplive.api.entity.dor.VoteDor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 09/02/2018.
 * back
 */
@Repository
public interface VoteDorRepository extends CrudRepository<VoteDor, Long> {
  List<VoteDor> findAllByRoundAndStudentIdAndQuestionDorAndSession(
    int round, Long student_id, QuestionDor questionDor, SessionDor session);

  List<VoteDor> findAllBySession_IdAndRound(Long session_id, int round);
  List<VoteDor> findAllBySession_IdAndRoundAndQuestionDor_id(Long session_id, int round, Long questionDor_id);
  List<VoteDor> findAllByStudent_IdAndSessionAndRound(Long student_id, SessionDor session, int round);
}
