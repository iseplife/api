package com.iseplife.api.dao.club;

import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {


  List<Club> findAllByOrderByName();

  @Query(
    "select c from Club c " +
      "join c.members m " +
      "where m.student = :student "
  )
  List<Club> findAllStudentClub(Student student);

  @Query(
    "select c from Club c " +
      "join c.members m " +
      "where m.student = :student " +
      "and m.role in :role"
  )
  List<Club> findByMemberRole(@Param("student") Student student, @Param("role") Set<ClubRole> roles);

  @Query(
    "select c from Club c " +
      "join c.members m " +
      "where m.student = :#{#student} " +
      "and m.role in :#{#role.getParents()} " +
      "and m.fromYear = :#{#year}"
  )
  List<Club> findCurrentByRoleWithInheritance(@Param("student") Student student, @Param("role") ClubRole role, @Param("year") Integer year);


  Page<Club> findAllByNameContainingIgnoringCase(String name, Pageable pageable);

}
