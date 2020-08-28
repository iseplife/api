package com.iseplife.api.dao.group;

import com.iseplife.api.entity.group.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
  Page<Group> findAll(Pageable page);

  Optional<Group> findOneByName(String name);

  @Query(
    "select distinct g.type from Group g"
  )
  List<Group> findDistinctType();

  @Query(
    "select g from Group g join GroupMember gm where " +
      "gm.student = ?1"
  )
  List<Group> findAllUserGroups(Long student);

}
