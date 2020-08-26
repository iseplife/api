package com.iseplife.api.dao.student;

import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
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
    student.setId(dto.getId());
    student.setBirthDate(dto.getBirthDate());
    student.setFirstName(dto.getFirstName());
    student.setLastName(dto.getLastName());
    student.setPromo(dto.getPromo());
    student.setMail(dto.getMail());
    return student;
  }

  public void updateDtoToEntity(Student student, StudentUpdateDTO dto) {
    student.setBirthDate(dto.getBirthDate());
    student.setMail(dto.getMail());

    student.setFacebook(dto.getFacebook());
    student.setTwitter(dto.getTwitter());
    student.setSnapchat(dto.getSnapchat());
    student.setInstagram(dto.getInstagram());
  }

  public void updateAdminDtoToEntity(Student student, StudentUpdateAdminDTO dto) {
    student.setFirstName(dto.getFirstName());
    student.setLastName(dto.getLastName());

    student.setBirthDate(dto.getBirthDate());

    student.setPromo(dto.getPromo());
    student.setMail(dto.getMail());

    student.setFacebook(dto.getFacebook());
    student.setTwitter(dto.getTwitter());
    student.setSnapchat(dto.getSnapchat());
    student.setInstagram(dto.getInstagram());
  }

  public static StudentPreview toPreview(Student student){
    StudentPreview studentPreview = new StudentPreview();

    studentPreview.setId(student.getId());
    studentPreview.setFirstName(student.getFirstName());
    studentPreview.setLastName(student.getLastName());
    studentPreview.setPromo(student.getPromo());
    studentPreview.setPicture(student.getPicture());

    return studentPreview;
  }

  public static StudentPreviewAdmin entityToPreviewAdmin(Student student){
    StudentPreviewAdmin studentPreviewAdmin = new StudentPreviewAdmin();

    studentPreviewAdmin.setId(student.getId());
    studentPreviewAdmin.setFirstName(student.getFirstName());
    studentPreviewAdmin.setLastName(student.getLastName());
    studentPreviewAdmin.setPromo(student.getPromo());
    studentPreviewAdmin.setPicture(student.getPicture());
    studentPreviewAdmin.setArchived(student.isArchived());
    studentPreviewAdmin.setRoles(
      student.getRoles()
        .stream()
        .map(Role::getRole)
        .collect(Collectors.toList())
    );

    return studentPreviewAdmin;
  }

  public static StudentAdminView entityToAdminView(Student student){
    StudentAdminView studentAdmin = new StudentAdminView();

    studentAdmin.setId(student.getId());

    studentAdmin.setFirstName(student.getFirstName());
    studentAdmin.setLastName(student.getLastName());
    studentAdmin.setPromo(student.getPromo());
    studentAdmin.setPicture(student.getPicture());

    studentAdmin.setMail(student.getMail());

    studentAdmin.setBirthDate(student.getBirthDate());
    studentAdmin.setFacebook(student.getFacebook());
    studentAdmin.setTwitter(student.getTwitter());
    studentAdmin.setInstagram(student.getInstagram());

    studentAdmin.setArchived(student.isArchived());
    studentAdmin.setRoles(
      student.getRoles()
        .stream()
        .map(Role::getRole)
        .collect(Collectors.toList())
    );

    return studentAdmin;
  }

}
