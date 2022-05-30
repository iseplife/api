package com.iseplife.api.services;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class StudentImportService {
  @Lazy final private StudentService studentService;
  final private StudentRepository studentRepository;

  public Student importStudents(Student newStudent, MultipartFile file) {

    Role studentRole = studentService.getRole(Roles.STUDENT);
    Set<Role> roles = new HashSet<>();
    roles.add(studentRole);

    Student student = new Student();
    student.setId(newStudent.getId());
    student.setFirstName(newStudent.getFirstName());
    student.setLastName(newStudent.getLastName());
    student.setFeed(new Feed(student.getName(), FeedType.STUDENT));
    student.setPromo(newStudent.getPromo());
    student.setRoles(roles);
    if (file != null) {
      student.setPicture(studentService.uploadOriginalPicture(null, file));
    }

    studentRepository.save(student);
    return student;
  }

}
