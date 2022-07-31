package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
import com.iseplife.api.entity.user.Student;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PollChoiceRepository extends CrudRepository<PollChoice, Long> {
  @Query(
      "select " +
        "p as p, " +
        "count(votedStudent) > 0 as voted " +
      "from PollChoice p " +
        "left join p.votes votedStudent on votedStudent.student = :student " +
      "where p.poll = :poll " +
      "group by p.id"
  )
  List<PollChoiceProjection> findAllByPoll(Poll poll, Student student);
  @Query(
      "select " +
        "p as p, " +
        "count(votedStudent) > 0 as voted " +
      "from PollChoice p " +
        "left join p.votes votedStudent on votedStudent.student.id = :studentId " +
      "where p.poll = :poll " +
      "group by p.id"
  )
  List<PollChoiceProjection> findAllByPoll(Poll poll, Long studentId);
  @Query(
      "select " +
        "p as p, " +
        "count(votedStudent) > 0 as voted " +
      "from PollChoice p " +
        "left join p.votes votedStudent on votedStudent.student.id = :studentId " +
      "where p.poll.id = :pollId " +
      "group by p.id"
  )
  List<PollChoiceProjection> findAllByPoll(Long pollId, Long studentId);
}
