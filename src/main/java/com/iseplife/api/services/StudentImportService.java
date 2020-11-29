package com.iseplife.api.services;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Guillaume on 22/10/2017.
 * back
 */
@Service
public class StudentImportService {

    private final Logger LOG = LoggerFactory.getLogger(StudentImportService.class);

    @Autowired
    StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    public Student importStudents(Student newStudent, MultipartFile file) {

        Role studentRole = studentService.getRole(Roles.STUDENT);
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);

        Student student = new Student();
        student.setId(newStudent.getId());
        student.setFirstName(newStudent.getFirstName());
        student.setLastName(newStudent.getLastName());
        student.setPromo(newStudent.getPromo());
        student.setRoles(roles);
        if (file != null) {
            student.setPicture(studentService.uploadOriginalPicture(null, file));
        }
        return student;
    }

}
