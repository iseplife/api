package com.iseplife.api.dao.student;

import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
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
import java.util.Set;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

  String GET_STUDENT_CACHE = "getStudentCache";
  String GET_STUDENT_BY_PROMO_CACHE = "getStudentByPromoCache";

  boolean existsById(Long id);

  Page<Student> findAllByOrderByLastName(Pageable pageable);

  @Cacheable(cacheNames = GET_STUDENT_CACHE)
  Optional<Student> findById(Long id);


  @Override
  @Caching(evict = {
    @CacheEvict(value = GET_STUDENT_CACHE, key = "#s.id"),
    @CacheEvict(value = GET_STUDENT_BY_PROMO_CACHE, key = "#s.promo")
  })
  <S extends Student> S save(S s);


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
      "where lower(concat(s.firstName, ' ', s.lastName)) like %?1%"
  )
  Page<Student> searchStudent(String name, Pageable pageable);

  @Query(
    "select s from Student s " +
      "where lower(concat(s.firstName, ' ', s.lastName)) like %?1% " +
      "and (?2 = true or s.archivedAt is null)"
  )
  List<Student> searchStudent(String name, Boolean active);

  @Query(
    "select s from Student s " +
      "where lower(concat(s.firstName, ' ', s.lastName)) like %?1% " +
      "and s.promo in ?2 "
  )
  List<Student> searchStudent(String name, List<String> promo);

  @Query(
    "select s from Student s " +
      "where lower(concat(s.firstName, ' ', s.lastName)) like %?1% " +
      "and s.promo in ?2 "
  )
  Page<Student> searchStudent(String name, List<Integer> promo, Pageable pageable);

  @Query(
    "select distinct s.promo from Student s order by s.promo desc"
  )
  List<Integer> findDistinctPromo();

  @Cacheable(cacheNames = GET_STUDENT_BY_PROMO_CACHE)
  List<Student> findAllByPromo(Integer promo);

}
