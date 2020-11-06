package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.JwtAuthRequest;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.group.GroupMemberRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.CASUserDTO;
import com.iseplife.api.dto.LDAPUserDTO;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.services.CASService;
import com.iseplife.api.services.LDAPService;
import com.iseplife.api.services.StudentService;
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
  StudentRepository studentRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  LDAPService ldapService;

  @Autowired
  CASService casService;

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
      if (authRequest.getUsername().equals("test") && authRequest.getPassword().equals(defaultPassword)) {
        return jwtTokenUtil.generateToken(studentService.getStudent(10552L));
      }
    }

    CASUserDTO user = casService.identifyToCAS(authRequest.getUsername(), authRequest.getPassword());


    throw new AuthException("User not found");
  }

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return studentService.getRoles();
  }

}
