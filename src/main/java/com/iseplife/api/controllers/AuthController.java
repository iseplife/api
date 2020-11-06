package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.JwtAuthRequest;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.constants.Roles;
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

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthController {

  private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  JwtTokenUtil jwtTokenUtil;

  @Autowired
  StudentService studentService;

  @Autowired
  RoleRepository roleRepository;


  @Autowired
  CASService casService;

  @Value("${auth.password}")
  String defaultPassword;

  @Value("${auth.enable}")
  Boolean passwordEnable;

  @Value("${auth.autoGeneration}")
  Boolean autoGeneration;

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
    Student student;
    try {
      student = studentService.getStudent(user.getNumero());
    } catch (IllegalArgumentException e) {
      if (autoGeneration) {
        LOG.info("User {} {} not found but pass authentification, creating account", user.getPrenom(), user.getPrenom());
        student = new Student();

        student.setId(user.getNumero());
        student.setFirstName(user.getPrenom());
        student.setLastName(user.getNom());
        student.setMail(user.getMail());

        String[] titre = user.getTitre().split("-");
        student.setPromo(Integer.valueOf(titre[2]));
        student.setRoles(Collections.singleton(roleRepository.findByRole(Roles.STUDENT)));
      }else {
        throw new AuthException("Identified User not found");
      }
    }

    if (student.isArchived())
      throw new AuthException("User archived");

    return jwtTokenUtil.generateToken(student);
  }

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return studentService.getRoles();
  }

}
