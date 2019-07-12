package com.iseplive.api.dao.student;

import com.iseplive.api.dto.student.StudentDTO;
import com.iseplive.api.dto.student.StudentUpdateAdminDTO;
import com.iseplive.api.dto.student.StudentUpdateDTO;
import com.iseplive.api.dto.view.StudentWithRoleView;
import com.iseplive.api.entity.user.Role;
import com.iseplive.api.entity.user.Student;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by Guillaume on 16/10/2017.
 * back
 */
@Component
public class StudentFactory {
  public Student dtoToEntity(StudentDTO dto) {
    Student student = new Student();
    student.setBio(dto.getBio());
    student.setBirthDate(dto.getBirthDate());
    student.setFirstName(dto.getFirstname());
    student.setLastName(dto.getLastname());
    student.setPhoneNumber(dto.getPhone());
    student.setPromo(dto.getPromo());
    student.setMail(dto.getMail());
    return student;
  }

  public void updateDtoToEntity(Student student, StudentUpdateDTO dto) {
    student.setBio(dto.getBio());
    student.setBirthDate(dto.getBirthDate());
    student.setPhoneNumber(dto.getPhone());
    student.setMail(dto.getMail());

    student.setFacebook(dto.getFacebook());
    student.setTwitter(dto.getTwitter());
    student.setSnapchat(dto.getSnapchat());
    student.setInstagram(dto.getInstagram());
  }

  public void updateAdminDtoToEntity(Student student, StudentUpdateAdminDTO dto) {
    student.setFirstName(dto.getFirstname());
    student.setLastName(dto.getLastname());

    student.setBio(dto.getBio());
    student.setBirthDate(dto.getBirthDate());
    student.setPhoneNumber(dto.getPhone());
    student.setPromo(dto.getPromo());
    student.setMail(dto.getMail());

    student.setFacebook(dto.getFacebook());
    student.setTwitter(dto.getTwitter());
    student.setSnapchat(dto.getSnapchat());
    student.setInstagram(dto.getInstagram());
  }

  public StudentWithRoleView studentToStudentWithRoles(Student student) {
    StudentWithRoleView studentWithRoleView = new StudentWithRoleView();

    studentWithRoleView.setId(student.getId());
    studentWithRoleView.setFirstname(student.getFirstName());
    studentWithRoleView.setLastname(student.getLastName());

    studentWithRoleView.setBio(student.getBio());
    studentWithRoleView.setBirthDate(student.getBirthDate());
    studentWithRoleView.setPhone(student.getPhoneNumber());
    studentWithRoleView.setPromo(student.getPromo());
    studentWithRoleView.setMail(student.getMail());

    studentWithRoleView.setFacebook(student.getFacebook());
    studentWithRoleView.setTwitter(student.getTwitter());
    studentWithRoleView.setSnapchat(student.getSnapchat());
    studentWithRoleView.setInstagram(student.getInstagram());

    studentWithRoleView.setId(student.getId());
    studentWithRoleView.setPhotoUrl(student.getPhotoUrl());
    studentWithRoleView.setPhotoUrlThumb(student.getPhotoUrlThumb());

    studentWithRoleView.setArchived(student.isArchived());

    studentWithRoleView.setRolesValues(
      student.getRoles().stream()
      .map(Role::getRole)
      .collect(Collectors.toList())
    );
    return studentWithRoleView;
  }
}
