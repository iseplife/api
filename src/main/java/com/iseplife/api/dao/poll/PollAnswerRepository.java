package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.media.poll.PollAnswer;
import com.iseplife.api.entity.media.poll.PollAnswer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface PollAnswerRepository extends CrudRepository<PollAnswer, Long> {
}
