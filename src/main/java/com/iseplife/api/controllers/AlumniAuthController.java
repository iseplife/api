package com.iseplife.api.controllers;

import javax.annotation.security.RolesAllowed;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.alumni.AlumniLoginDTO;
import com.iseplife.api.dto.alumni.AlumniSetPasswordDTO;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpUnauthorizedException;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/alumni-auth")
@RequiredArgsConstructor
public class AlumniAuthController {
  final private StudentService studentService;
  final private SecurityService securityService;
  
  @PostMapping("change-password")
  @RolesAllowed({Roles.STUDENT})
  public void setAlumniPassword(@RequestBody AlumniSetPasswordDTO body) {
    if(body.getPassword().length() < 5) {
      throw new HttpBadRequestException("Password is not long enough");
    }
    studentService.updatePassword(SecurityService.getLoggedId(), body.getPassword());
  }

  @PostMapping("login")
  public TokenSet login(@RequestBody AlumniLoginDTO body) {
    Student student = studentService.getStudent(body.getStudentId());
    if(student.getPassword() == null || student.getPassword().length() == 0) {
      System.out.println("User "+body.getStudentId()+" has no password set");
      throw new HttpUnauthorizedException("authentification_failed");
    }
    if(!BCrypt.checkpw(body.getPassword(), student.getPassword())) {
      System.out.println("User "+body.getStudentId()+" logged in with wrong password");
      throw new HttpUnauthorizedException("authentification_failed");
    }
    
    System.out.println(student.getName()+" logging in with Alumni Auth");
  
    if (student.isArchived())
      throw new HttpUnauthorizedException("authentification_failed");
    
    return securityService.logUser(student);
  }
}
