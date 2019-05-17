package com.iseplive.api.dao.student;

import com.iseplive.api.entity.user.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by Guillaume on 08/08/2017.
 * back
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
  Role findByRole(String name);
  List<Role> findAll();
  Set<Role> findAllByIdIn(List<Long> id);
}
