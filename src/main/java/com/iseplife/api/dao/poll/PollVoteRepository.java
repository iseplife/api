package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.media.poll.PollVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface PollVoteRepository extends CrudRepository<PollVote, Long> {
  PollVote findByAnswer_IdAndStudent_Id(Long answer_id, Long student_id);

  List<PollVote> findByAnswer_Poll_IdAndStudent_Id(Long pollid, Long studentid);

  List<PollVote> findByAnswer_Poll_Id(Long pollId);
}
