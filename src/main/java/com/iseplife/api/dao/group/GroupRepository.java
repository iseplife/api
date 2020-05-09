package com.iseplife.api.dao.group;

import com.iseplife.api.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
  Page<Group> findAll(Pageable page);

  @Query(
    "select distinct g.type from Group g"
  )
  List<Group> findDistinctType();
}
