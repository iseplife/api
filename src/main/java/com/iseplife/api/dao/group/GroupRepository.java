package com.iseplife.api.dao.group;

import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {

  String FIND_ALL_USER_GROUPS_CACHE = "findAllUserGroupsCache";
  String FIND_GROUP_CACHE = "getFeedCache";

  @Override
  @Cacheable(cacheNames = FIND_GROUP_CACHE)
  Optional<Group> findById(Long id);

  @Override
  @CacheEvict(value = FIND_GROUP_CACHE, key = "#group.id")
  <T extends Group> T save(T group);

  @Override
  @CacheEvict(value = FIND_GROUP_CACHE, key = "#group.id")
  void delete(Group group);

  Page<Group> findAll(Pageable page);

  Optional<Group> findOneByName(String name);

  @Query(
    "select distinct g.type from Group g"
  )
  List<Group> findDistinctType();


  @Query(
    "select distinct g from Group g join g.members m " +
      "where lower(g.name) like %?1% " +
      "and (?3 = true or g.restricted = false or m.student.id = ?2)"
  )
  Page<Group> searchGroup(String name, Long student, Boolean admin, Pageable pageable);


  @Cacheable(value = FIND_ALL_USER_GROUPS_CACHE, key = "#studentId")
  @Query(
    "select g from Group g left join g.members gm where " +
      "gm.student.id = :studentId"
  )
  List<Group> findAllUserGroups(Long studentId);

}
