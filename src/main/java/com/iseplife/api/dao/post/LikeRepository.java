package com.iseplife.api.dao.post;

import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

  boolean existsByThread_IdAndStudent_Id(Long thread, Long student);

  Like findOneByThreadIdAndStudentId(Long thread, Long student);
}
