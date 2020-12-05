package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.post.embed.poll.PollVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PollVoteRepository extends CrudRepository<PollVote, Long> {
  Optional<PollVote> findByChoice_IdAndStudent_Id(Long answer_id, Long student_id);

  List<PollVote> findByChoice_Poll_IdAndStudent_Id(Long pollId, Long studentId);

  List<PollVote> findByChoice_Poll_Id(Long pollId);
}
