package com.iseplive.api.dao.employee;

import com.iseplive.api.dto.EmployeeDTO;
import com.iseplive.api.entity.user.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeFactory {
  public Employee dtoToEntity(EmployeeDTO employeeDTO) {
    Employee employee = new Employee();
    employee.setFirstname(employeeDTO.getFirstname());
    employee.setLastname(employeeDTO.getLastname());
    return employee;
  }
}
