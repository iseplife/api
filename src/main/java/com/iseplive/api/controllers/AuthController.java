package com.iseplive.api.controllers;

import com.iseplive.api.conf.jwt.JwtAuthRequest;
import com.iseplive.api.conf.jwt.JwtTokenUtil;
import com.iseplive.api.conf.jwt.TokenSet;
import com.iseplive.api.dto.LDAPUserDTO;
import com.iseplive.api.entity.user.Role;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.services.LDAPService;
import com.iseplive.api.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  JwtTokenUtil jwtTokenUtil;

  @Autowired
  StudentService studentService;

  @Autowired
  LDAPService ldapService;

  @Value("${auth.password}")
  String defaultPassword;

  @Value("${auth.enable}")
  Boolean passwordEnable;

  @PostMapping
  public TokenSet getToken(@RequestBody JwtAuthRequest authRequest) {

    // TODO: replace with correct auth, currently only for testing
    if (passwordEnable) {
      if (authRequest.getUsername().equals("admin") && authRequest.getPassword().equals(defaultPassword)) {
        return jwtTokenUtil.generateToken(studentService.getStudent(1L));
      }
    }

    LDAPUserDTO user = ldapService.retrieveUser(authRequest.getUsername(), authRequest.getPassword());
    if (user != null) {
      Student ldapStudent = studentService.getStudent(user.getEmployeeNumber());
      if (ldapStudent == null || ldapStudent.isArchived()) {
        throw new AuthException("User not found");
      }
      return jwtTokenUtil.generateToken(ldapStudent);
    }

    throw new AuthException("User not found");
  }

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return studentService.getRoles();
  }

}
