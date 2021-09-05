package com.iseplife.api.dao.post;

import com.iseplife.api.entity.post.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

  @Query(
    "select c as comment, " +
        "c.thread.id as thread, " +
        "case when count(l) = 1 then true else false end as liked, " +
        "size(c.thread.comments) as comments, " +
        "size(c.thread.likes) as likes " +
      "from Comment c " +
      "join c.thread.likes l on l.student.id = ?2 " +
      "where c.parentThread.id = ?1 " +
      "order by c.thread.likes.size desc "
  )
  CommentProjection findTrendingComments(Long thread, Long loggedUser);
}
