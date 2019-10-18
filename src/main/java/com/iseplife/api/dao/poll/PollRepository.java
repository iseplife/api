package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.media.poll.Poll;
import com.iseplife.api.entity.media.poll.Poll;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {
}
