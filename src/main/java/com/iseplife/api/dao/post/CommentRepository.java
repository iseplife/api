package com.iseplife.api.dao.post;

import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.entity.post.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

  @Query(
    "select c " +
    "from Comment c " +
      "where c.parentThread.id = ?1 " +
      "order by size(c.thread.likes) desc"
  )
  List<CommentProjection> findTrendingComments(Long thread, Long loggedUser, Pageable page);

  @Query(
    "select c " +
    "from Comment c " +
      "where c.parentThread.id = ?1 " +
      "order by c.creation desc"
  )
  List<CommentProjection> findThreadComments(Long thread);
}
