package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.JwtAuthRequest;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.dto.ISEPCAS.CASUserDTO;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.exceptions.http.HttpUnauthorizedException;
import com.iseplife.api.services.CASService;
import com.iseplife.api.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  final private JwtTokenUtil jwtTokenUtil;
  final private CASService casService;
  final private StudentService studentService;
  final private Logger LOG = LoggerFactory.getLogger(AuthController.class);

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
        LOG.info("User {} {} not found but pass authentication, creating account", user.getPrenom(), user.getPrenom());
        student = new Student();
        studentService.hydrateStudent(student, user);
      } else {
        throw new HttpNotFoundException("user_not_found");
      }
    }

    if (student.isArchived())
      throw new HttpNotFoundException("user_not_found");

    if (student.getLastConnection() == null) {
      LOG.info("First connection for user {}, hydrating account", student.getId());
      studentService.hydrateStudent(student, user);
    }

    return jwtTokenUtil.generateToken(student);
  }

  @PutMapping("/logout")
  public void logoutCurrentUser(HttpServletResponse response) {
    Cookie expiredCookie = new Cookie("refresh-token", null);
    expiredCookie.setMaxAge(0);
    expiredCookie.setSecure(true);
    expiredCookie.setHttpOnly(true);
    expiredCookie.setPath("/");

    response.addCookie(expiredCookie);
  }

  @PostMapping("/refresh")
  public TokenSet getRefreshedTokens(@CookieValue(value = "refresh-token", defaultValue = "") String refreshToken) {
    if (refreshToken.equals(""))
      throw new HttpUnauthorizedException("refresh_token_expired");

    return jwtTokenUtil.refreshWithToken(refreshToken);
  }

  @GetMapping("/roles")
  public List<Role> getRoles() {
    return studentService.getRoles();
  }

}
