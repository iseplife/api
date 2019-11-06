package com.iseplife.api.dao.post;

import com.iseplife.api.entity.Thread;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends CrudRepository<Thread, Long> {
}
