package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.club.view.ClubMemberPreview;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.dto.student.view.StudentView;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.services.*;
import com.iseplife.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

/**
 * Created by Guillaume on 29/07/2017.
 * back
 */
@RestController
@RequestMapping("/student")
public class StudentController {

  @Autowired
  StudentService studentService;

  @Autowired
  PostService postService;

  @Autowired
  ClubService clubService;

  @Autowired
  StudentImportService studentImportService;

  @Autowired
  MediaService mediaService;

  @Autowired
  JsonUtils jsonUtils;


  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public StudentPreview getLoggedStudentPreview() {
    return studentService.getLoggedStudentPreview();
  }

  @GetMapping("/me/full")
  @RolesAllowed({Roles.STUDENT})
  public StudentView getLoggedStudent() {
    return studentService.getLoggedStudent();
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public StudentOverview getStudent(@PathVariable Long id) {
    return StudentFactory.toOverview(studentService.getStudent(id));
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostView> getPostsStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return postService.getPostsAuthor(id, SecurityService.isUserAnonymous(), page);
  }

  @GetMapping("/{id}/photo")
  @RolesAllowed({Roles.STUDENT})
  public Page<MatchedView> getPhotosStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return mediaService.getPhotosTaggedByStudent(id, page);
  }

  @GetMapping("/{id}/club")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubMemberPreview> getStudentClubs(@PathVariable Long id) {
    return clubService.getStudentClubs(id);
  }

  @PutMapping("/{id}/archive")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Boolean toggleArchiveStatus(@PathVariable Long id) {
    return studentService.toggleArchiveStudent(id);
  }

  @GetMapping("/{id}/roles")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Set<Role> getStudentRoles(@PathVariable Long id) {
    return studentService.getStudentRoles(id);
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView createStudent(@RequestParam StudentDTO dto) {
    return studentService.createStudent(dto);
  }

  @GetMapping("/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Page<StudentPreviewAdmin> getAllStudentsAdmin(@RequestParam(defaultValue = "0") int page) {
    return studentService.getAllForAdmin(page);
  }

  @GetMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN})
  public StudentAdminView getStudentAdmin(@PathVariable Long id) {
    return StudentFactory.toAdminView(studentService.getStudent(id));
  }

  @PutMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView updateStudentAdmin(@PathVariable Long id, @RequestBody StudentUpdateAdminDTO dto) {
    return studentService.updateStudentAdmin(id, dto);
  }

  @PostMapping("/import")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView importStudents(@ModelAttribute Student student, @RequestParam(value = "file",
          required = false) MultipartFile file) {
    return StudentFactory.toAdminView(studentImportService.importStudents(student, file));
  }

  @GetMapping("/promos")
  @RolesAllowed({Roles.STUDENT})
  public List<Integer> getAllPromos() {
    return studentService.getAllPromo();
  }
}
