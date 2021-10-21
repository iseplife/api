package com.iseplife.api.dao.post;

import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Like;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.post.Like;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

  String EXISTS_BY_THREAD_AND_STUDENT_CACHE = "existsByThreadAndStudentCache";

  @Cacheable(cacheNames = EXISTS_BY_THREAD_AND_STUDENT_CACHE, key = "{#thread, #student}")
  boolean existsByThread_IdAndStudent_Id(Long thread, Long student);

  Like findOneByThreadIdAndStudentId(Long thread, Long student);
  
  int countByThreadId(Long thread);
}
