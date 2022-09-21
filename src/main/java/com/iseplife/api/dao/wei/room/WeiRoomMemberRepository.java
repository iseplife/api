package com.iseplife.api.dao.wei.room;

import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.wei.WeiRoom;
import com.iseplife.api.entity.wei.WeiRoomMember;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WeiRoomMemberRepository extends CrudRepository<WeiRoomMember, Student> {
  boolean existsByStudentId(Long studentId);
  
  Optional<WeiRoomMember> findByStudentId(Long studentId);
  
  @Transactional
  @Modifying
  void deleteByRoom(WeiRoom room);
}
