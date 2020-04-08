package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.view.ImportStudentResultView;
import com.iseplife.api.dto.view.ClubMemberView;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.dto.view.StudentWithRoleView;
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
  AuthService authService;

  @Autowired
  MediaService mediaService;

  @Autowired
  JsonUtils jsonUtils;

  @GetMapping
  @RolesAllowed({Roles.ADMIN})
  public Page<StudentPreview> getAllStudents(@RequestParam(defaultValue = "0") int page) {
    return studentService.getAll(page);
  }

  @GetMapping("/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Page<StudentPreviewAdmin> getAllStudentsAdmin(@RequestParam(defaultValue = "0") int page) {
    return studentService.getAllForAdmin(page);
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView createStudent(@RequestBody StudentDTO dto) {
    return studentService.createStudent(dto);
  }

  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public Student getLoggedStudent(@AuthenticationPrincipal TokenPayload auth) {
    return studentService.getStudent(auth.getId());
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public Student getStudent(@PathVariable Long id) {
    return studentService.getStudent(id);
  }

  @GetMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN})
  public StudentAdminView getStudentAdmin(@PathVariable Long id) {
    return StudentFactory.entityToAdminView(studentService.getStudent(id));
  }

  @PutMapping
  @RolesAllowed({Roles.STUDENT})
  public Student updateStudent(@AuthenticationPrincipal TokenPayload auth, @RequestBody StudentUpdateDTO dto) {
    return studentService.updateStudent(dto, auth.getId());
  }

  @PutMapping("/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView updateStudentAdmin(@RequestBody StudentUpdateAdminDTO form) {
    return studentService.updateStudentAdmin(form);
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostView> getPostsStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return postService.getPostsAuthor(id, authService.isUserAnonymous(), page);
  }

  @GetMapping("/{id}/photo")
  @RolesAllowed({Roles.STUDENT})
  public Page<MatchedView> getPhotosStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return mediaService.getPhotosTaggedByStudent(id, page);
  }

  @GetMapping("/{id}/club")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubMemberView> getClubsStudent(@PathVariable Long id) {
    return clubService.getStudentClubs(id);
  }

  @PutMapping("/{id}/archive")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView toggleArchiveStatus(@PathVariable Long id) {
    return studentService.toggleArchiveStudent(id);
  }

  @PutMapping("/notification")
  @RolesAllowed({Roles.STUDENT})
  public void toggleNotification(@AuthenticationPrincipal TokenPayload auth) {
    studentService.toggleNotifications(auth);
  }

  @GetMapping("/{id}/roles")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Set<Role> getStudentRoles(@PathVariable Long id) {
    return studentService.getStudentRoles(id);
  }

  @PostMapping("/import")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public ImportStudentResultView importStudents(@RequestParam("csv") MultipartFile csv,
                                                @RequestParam("images[]") List<MultipartFile> photos) {
    return studentImportService.importStudents(csv, photos);
  }

  @GetMapping("/promos")
  @RolesAllowed({Roles.STUDENT})
  public List<String> getAllPromos() {
    return studentService.getAllPromo();
  }
}
