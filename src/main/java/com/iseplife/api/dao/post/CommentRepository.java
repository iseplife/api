package com.iseplife.api.dao.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

  @Query(
    "select c from Comment c where c.parentThread.id = ?1 " +
      "order by c.thread.likes.size desc "
  )
  Page<Comment> findTrendingComments(Long thread, Pageable pageable);
}
