package com.iseplive.api.services;

import com.iseplive.api.constants.Roles;
import com.iseplive.api.dao.student.StudentRepository;
import com.iseplive.api.dto.view.ImportStudentResultView;
import com.iseplive.api.entity.user.Role;
import com.iseplive.api.entity.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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

  public ImportStudentResultView importStudents(MultipartFile csv, List<MultipartFile> photos) {

    ImportStudentResultView res = new ImportStudentResultView();

    // Map of studentID : Student
    Map<Long, Student> students = new HashMap<>();
    try {
      String line;
      InputStream is = csv.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      int lineNum = 0;

      Role studentRole = studentService.getRole(Roles.STUDENT);
      Set<Role> roles = new HashSet<>();
      roles.add(studentRole);
      while ((line = br.readLine()) != null) {
        String csvSeparator = ",";
        String[] cols = line.split(csvSeparator);
        int csvNumColumns = 4;
        if (lineNum != 0 && cols.length == csvNumColumns) {
          String firstname = cols[0];
          String lastname = cols[1];
          Long studentId = Long.parseLong(cols[2]);
          String promo = cols[3];

          Student student = new Student();
          student.setId(studentId);
          student.setFirstName(firstname);
          student.setLastName(lastname);
          student.setPromo(promo);
          student.setRoles(roles);

          students.put(studentId, student);
        }

        lineNum++;
      }

      res = createStudents(photos, students);


    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return res;
  }

  private ImportStudentResultView createStudents(List<MultipartFile> photos, Map<Long, Student> students) {
    ImportStudentResultView res = new ImportStudentResultView();
    res.setPhotosSent(photos.size());
    res.setStudentsSent(students.size());

    List<Student> studentsToCreate = new ArrayList<>();
    Map<Long, MultipartFile> photosToAdd = new HashMap<>();

    for (MultipartFile photo : photos) {
      String fullname = photo.getOriginalFilename();
      Long idIsep = Long.parseLong(fullname.split("\\.")[0]);
      photosToAdd.put(idIsep, photo);
    }

    students.forEach((e, s) -> {
      Student student = studentService.getStudent(e);
      // student already exist
      if (student != null) {
        res.incrAlreadyImported();
        // add image if not present
        if (student.getPhotoUrl() == null || student.getPhotoUrlThumb() == null) {
          // check if photo can be added
          if (photosToAdd.get(student.getId()) != null) {
            studentService.addProfileImage(student.getId(), photosToAdd.get(student.getId()));
            res.incrPhotoAdded();
            res.incrStudentPhotoNotMatched();
          }
        }
      } else { // if user doesn't exist

        // check photo exist
//        if (photosToAdd.get(s.getStudentId()) != null) {
        studentsToCreate.add(s);
        res.incrImport();
//        } else {
//          res.incrStudentPhotoNotMatched();
//        }
      }
    });

    studentRepository.save(studentsToCreate);
    // add photo to new students
    for (Student s : studentsToCreate) {
      if (photosToAdd.get(s.getId()) != null) {
        studentService.addProfileImage(s.getId(), photosToAdd.get(s.getId()));
        res.incrPhotoAdded();
      }
    }

    return res;
  }

}
