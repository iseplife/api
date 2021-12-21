package com.iseplife.api.dao.survey;

import com.iseplife.api.entity.survey.Survey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface SurveyRepository extends CrudRepository<Survey, Long> {


  @Query(
    "select distinct s " +
      "from Survey s " +
      "join s.choices c " +
      "join s.targets t " +
      "where s.enabled = ?1 " +
      "and s.opensAt >= ?2 " +
      "and s.closesAt < ?2 " +
      "and t.id in(?3) " +
      "order by s.closesAt asc"
  )
  Set<Survey> findCurrentSurveys(Boolean enabled, Date date, List<Long> feeds);
}
