package com.iseplife.api.dao.student;

import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

  boolean existsById(Long id);

  Page<Student> findAllByOrderByLastName(Pageable pageable);

  @Query(
    "select s from Student s " +
      "join s.roles r " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% and r in ?2 and s.promo in ?3 " +
      "and s.archivedAt is null"
  )
  Page<Student> searchStudentRolePromo(String name, Set<Role> roles, List<Integer> promo, Pageable pageable);

  @Query(
    "select s from Student s " +
      "join s.roles r " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% and r in ?2 " +
      "and s.archivedAt is null"
  )
  Page<Student> searchStudentRole(String name, Set<Role> roles, Pageable pageable);

  @Query(
    "select s from Student s " +
      "where unaccent(lower(concat(s.firstName, ' ', s.lastName))) like '%' || unaccent(?1) || '%'"
  )
  Page<Student> searchStudent(String name, Pageable pageable);

  @Query(
    "select s from Student s " +
      "where unaccent(lower(concat(s.firstName, ' ', s.lastName))) like '%' || unaccent(?1) || '%' " +
      "and (?2 = true or s.archivedAt is null)"
  )
  List<Student> searchStudent(String name, Boolean active);

  @Query(
    "select s from Student s " +
      "where unaccent(lower(concat(s.firstName, ' ', s.lastName))) like '%' || unaccent(?1) || '%' " +
      "and s.promo in ?2 "
  )
  List<Student> searchStudent(String name, List<String> promo);

  @Query(
    "select s from Student s " +
      "where unaccent(lower(concat(s.firstName, ' ', s.lastName))) like '%' || unaccent(?1) || '%' " +
      "and s.promo in ?2 "
  )
  Page<Student> searchStudent(String name, List<Integer> promo, Pageable pageable);

  @Query(
    "select distinct s.promo from Student s order by s.promo desc"
  )
  List<Integer> findDistinctPromo();

  List<Student> findAllByPromo(Integer promo);

  @Transactional
  @Modifying
  @Query("update Student s set s.lastExploreWatch = :date where s.id = :loggedUser")
  void updateLastExplore(Long loggedUser, Date date);

  @Transactional
  @Modifying
  @Query("update Student s set s.password = :password where s.id = :loggedUser")
  void updatePassword(Long loggedUser, String password);
  
  @Query(
    "select " +
      "s.id " +
    "from Student s " +
      "inner join ClubMember cm on cm.student = s and cm.club = :club"
  )
  List<Long> findAllEditorIdByClub(Club club);
  
  @Query(
    "select " +
      "s.id " +
    "from Student s " +
      "inner join GroupMember gm on gm.student = s and gm.group = :group"
  )
  List<Long> findAllIdByGroup(Group group);

}
