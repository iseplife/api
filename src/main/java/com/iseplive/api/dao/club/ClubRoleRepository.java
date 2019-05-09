package com.iseplive.api.dao.club;

import com.iseplive.api.entity.club.ClubRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Repository
public interface ClubRoleRepository extends CrudRepository<ClubRole, Long> {
  ClubRole findOneByName(String name);

  List<ClubRole> findByClub_Id(Long id);

}
