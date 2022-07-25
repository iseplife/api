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
    "select c as comment, " +
      "c.thread.id as thread, " +
      "size(c.thread.comments) as comments, " +
      "size(c.thread.likes) as likes, " +
      "count(likeStudent) > 0 as liked " +
    "from Comment c " +
      "join c.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = ?2 " +
      "where c.parentThread.id = ?1 " +
      "group by c, t " +
      "order by size(c.thread.likes) desc"
  )
  List<CommentProjection> findTrendingComments(Long thread, Long loggedUser, Pageable page);

  @Query(
    "select " +
      "c as comment, " +
      "t.id as thread, " +
      "size(t.comments) as comments, " +
      "size(l) as likes, " +
      "count(likeStudent) > 0 as liked " +
    "from Comment c " +
      "join c.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = ?2 " +
      "where c.parentThread.id = ?1 " +
      "group by c, t " +
      "order by c.creation desc"
  )
  List<CommentProjection> findThreadComments(Long thread, Long loggedUser);
}
