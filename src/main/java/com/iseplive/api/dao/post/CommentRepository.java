package com.iseplive.api.dao.post;

import com.iseplive.api.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
