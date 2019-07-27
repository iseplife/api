package com.iseplive.api.dao.post;

import com.iseplive.api.entity.post.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

  Like findOneByCommentIdAndStudentId(Long comment, Long student);

  Like findOneByPostIdAndStudentId(Long post, Long student);
}
