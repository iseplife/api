package com.iseplife.api.dao.poll;

import com.iseplife.api.entity.post.embed.poll.PollChoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PollChoiceRepository extends CrudRepository<PollChoice, Long> {
}
