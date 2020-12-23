package com.iseplife.api.dao.group;

import com.iseplife.api.entity.GroupMember;
import com.iseplife.api.entity.group.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Long> {

  @Query(
    "select case when (count(m.id) > 0)  then true else false end " +
      "from GroupMember m where " +
      "m.group.id= ?1 and " +
      "m.student.id = ?2"
  )
  Boolean isMemberOfGroup(Long id, Long student);

  List<GroupMember> findByGroup_Id(Long groupId);

  @Query(
    "select count(m) from GroupMember m " +
      "where m.group = ?1 and " +
      "m.admin = true "
  )
  Integer findGroupAdminCount(Group group);
}
