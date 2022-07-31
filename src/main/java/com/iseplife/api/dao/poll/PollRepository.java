package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.user.Student;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {
  @Query("select p from Poll p where p.id = :id")
  PollProjection findProjectionById(Long id);
}
