package com.iseplive.api.dao.club;

import com.iseplive.api.constants.ClubRoleEnum;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.user.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {
  List<Club> findAllByOrderByName();

  @Query("select c from Club c where c.members = :student and c.members.role = :role")
  List<Club> findByMemberRole(@Param("student") Student student, @Param("role") ClubRoleEnum role);

  List<Club> findAllByNameContainingIgnoringCase(String name);

  @Query("select c from Club c where c.members = :student or c.members.role = :role")
  List<Club> findByAdminsContains(Student admin);

  Club findByIsAdmin(Boolean isAdmin);
}
