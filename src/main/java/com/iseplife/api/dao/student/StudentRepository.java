package com.iseplife.api.dao.student;

import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
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
      "where lower(concat(s.firstName, ' ', s.lastName)) " +
            "like %?1% "
  )
  Page<Student> searchStudent(String name, Pageable pageable);

  @Query(
          "select s from Student s " +
                  "where lower(concat(s.firstName, ' ', s.lastName)) like %?1%"
  )
  List<Student> searchStudent(String name);

  @Query(
          "select s from Student s " +
                  "where lower(concat(s.firstName, ' ', s.lastName)) like %?1% " +
                  "and s.promo in ?2 "
  )
  List<Student> searchStudent(String name, List<String> promo);

  @Query(
          "select s from Student s " +
                  "where lower(concat(s.firstName, ' ', s.lastName)) " +
                  "like %?1% and s.promo in ?2 "
  )
  Page<Student> searchStudent(String name, List<Integer> promo, Pageable pageable);

  @Query(
          "select distinct s.promo from Student s order by s.promo desc"
  )
  List<String> findDistinctPromo();

}
