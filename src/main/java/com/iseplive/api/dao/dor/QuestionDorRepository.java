package com.iseplive.api.dao.dor;

import com.iseplive.api.entity.dor.QuestionDor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guillaume on 09/02/2018.
 * back
 */
@Repository
public interface QuestionDorRepository extends CrudRepository<QuestionDor, Long> {
  List<QuestionDor> findAllByOrderByPosition();

  @Modifying
  @Transactional
  @Query("UPDATE QuestionDor q SET q.position = q.position - 1 WHERE q.position >= :position")
  int updatePosAfterDelete(@Param("position") int position);

  @Modifying
  @Transactional
  @Query("UPDATE QuestionDor q SET q.position = q.position - 1 WHERE q.position > :position AND q.position <= :dest")
  int beforeMoveToPosInc(@Param("position") int position, @Param("dest") int dest);

  @Modifying
  @Transactional
  @Query("UPDATE QuestionDor q SET q.position = q.position + 1 WHERE q.position < :position AND q.position >= :dest")
  int beforeMoveToPosDec(@Param("position") int position, @Param("dest") int dest);


  QuestionDor findByPosition(int position);
}
