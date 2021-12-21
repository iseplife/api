package com.iseplife.api.dao.survey;

import com.iseplife.api.entity.survey.SurveyVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SurveyVoteRepository extends CrudRepository<SurveyVote, Long> {

  @Query(
    "select v " +
      "from SurveyVote v " +
      "join v.choice c " +
      "join c.survey s " +
      "where s.id = ?1 " +
      "and v.id = ?2 "
  )
  List<SurveyVote> findAllByVoterAndSurvey(Long surveyId, Long voterId);
}
