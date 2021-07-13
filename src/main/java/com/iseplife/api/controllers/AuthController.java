package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.JwtAuthRequest;
import com.iseplife.api.conf.jwt.JwtRefreshRequest;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dto.CASUserDTO;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.services.CASService;
import com.iseplife.api.services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthController {

  private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  JwtTokenUtil jwtTokenUtil;

  @Autowired
  CASService casService;

  @Autowired
  StudentService studentService;

  @Autowired
  RoleRepository roleRepository;


  @Value("${auth.password-root}")
  String defaultPassword;

  @Value("${auth.enable-root}")
  Boolean passwordEnable;

  @Value("${auth.auto-generation}")
  Boolean autoGeneration;

  @PostMapping
  public TokenSet getToken(@RequestBody JwtAuthRequest authRequest) {
    if (
      passwordEnable &&
        authRequest.getUsername().equals("admin") &&
        authRequest.getPassword().equals(defaultPassword)
    ) return jwtTokenUtil.generateToken(studentService.getStudent(1L));

    CASUserDTO user = casService.identifyToCAS(authRequest.getUsername(), authRequest.getPassword());
    Student student;
    try {
      student = studentService.getStudent(user.getNumero());
    } catch (IllegalArgumentException e) {
      if (autoGeneration) {
        LOG.info("User {} {} not found but pass authentification, creating account", user.getPrenom(), user.getPrenom());
        student = new Student();
        studentService.hydrateStudent(student, user);
      } else {
        throw new AuthException("Identified User not found");
      }
    }

    if (student.getLastConnection() == null) {
      LOG.info("First connection for user {}, hydrating account", student.getId());
      studentService.hydrateStudent(student, user);
    }

    if (student.isArchived())
      throw new AuthException("User archived");

    return jwtTokenUtil.generateToken(student);
  }

  @PostMapping("/refresh")
  public TokenSet getRefreshedTokens(@RequestBody JwtRefreshRequest request) {
    return jwtTokenUtil.refreshWithToken(request.getRefreshToken());
  }

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return studentService.getRoles();
  }

}
