package com.iseplive.api.dao.employee;

import com.iseplive.api.entity.user.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  @Query("select e from Employee e where lower(concat(e.firstname, ' ', e.lastname)) like %?1%")
  List<Employee> searchEmployeesByName(String name);

  List<Employee> findAll();

}
