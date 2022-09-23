package com.iseplife.api.dao.wei.map;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.dao.wei.map.projection.WeiMapStudentLocationProjection;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.wei.map.WeiMapStudentLocation;

@Repository
public interface WeiMapStudentLocationRepository extends CrudRepository<WeiMapStudentLocation, Integer> {
  @Query("select l from WeiMapStudentLocation l")
  List<WeiMapStudentLocationProjection> findAllProjections();
  
  @Query("select l from WeiMapStudentLocation l inner join Subscription s on s.listener = l.student and s.listener.id != :studentId and s.subscribed.id = :studentId")
  List<WeiMapStudentLocationProjection> findFollowed(Long studentId);
  
  @Transactional
  @Modifying
  @Query("update WeiMapStudentLocation l set l.student = null where l.student = :student and l.id != :newId")
  void anonymiseOtherProjections(Long newId, Student student);
}
