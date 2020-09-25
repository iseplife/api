package com.iseplife.api.dao.group;

import com.iseplife.api.entity.GroupMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Long> {

  @Query(
    "select case when (count(m.id) > 0)  then true else false end " +
      "from GroupMember m where " +
      "m.group.id= ?1 and " +
      "m.student.id = ?2"
  )
  Boolean isMemberOfGroup(Long id, Long student);
}
