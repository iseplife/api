package com.iseplife.api.dao.thread;

import com.iseplife.api.dto.thread.view.ThreadProjection;
import com.iseplife.api.entity.Thread;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends CrudRepository<Thread, Long> {

  @Query(
    "select " +
      "t.id as id, " +
      "size(t.comments) as nbComments, " +
      "size(t.likes) as nbLikes, " +
      "case when count(student) > 0 then true else false end as liked " +
    "from Thread t " +
      "left join t.likes likes " +
      "left join likes.student student on student.id = :loggedStudent " +
    "where t.id = :id group by t.id"
  )
  ThreadProjection findThreadById(Long id, Long loggedStudent);

  @Query("select count(parentComment) > 0 from Comment c join Comment parentComment on parentComment.thread = c.parentThread where c.thread = :thread")
  Boolean doesParentCommentExist(Thread thread);
}
