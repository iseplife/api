package com.iseplive.api.dao.student;

import com.iseplive.api.entity.user.Role;
import com.iseplive.api.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
  Page<Student> findAll(Pageable pageable);

  @Query(
    "select s from Student s " +
      "join s.roles r " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% and r in ?2 and s.promo in ?3 " +
      "and s.archived = false"
  )
  Page<Student> searchStudentRolePromo(String name, Set<Role> roles, List<Integer> promo, Pageable pageable);

  @Query(
    "select s from Student s " +
      "join s.roles r " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% and r in ?2 " +
      "and s.archived = false"
  )
  Page<Student> searchStudentRole(String name, Set<Role> roles, Pageable pageable);

  @Query(
    "select s from Student s " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% " +
      "and s.archived = false"
  )
  Page<Student> searchStudent(String name, Pageable pageable);

  @Query(
    "select s from Student s " +
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
      "like %?1% and s.promo in ?2 " +
      "and s.archived = false"
  )
  Page<Student> searchStudent(String name, List<Integer> promo, Pageable pageable);

}
