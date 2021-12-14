package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentSettingsDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.view.*;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
  final private PostService postService;
  final private StudentImportService studentImportService;
  final private MediaService mediaService;
  final private StudentService studentService;
  final private NotificationService notificationService;
  final private StudentFactory factory;

  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public StudentPreview getLoggedStudentPreview() {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    return factory.toPreview(student, notificationService.countUnwatchedNotifications(student));
  }

  @GetMapping("/me/full")
  @RolesAllowed({Roles.STUDENT})
  public StudentView getLoggedStudent(@AuthenticationPrincipal TokenPayload token) {
    return factory.toView(studentService.getStudent(token.getId()));
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public StudentOverview getStudent(@PathVariable Long id) {
    return factory.toOverview(studentService.getStudent(id));
  }

  @PostMapping("/me/picture")
  @RolesAllowed({Roles.STUDENT})
  public StudentPictures updatePicture(@RequestBody MultipartFile file) {
    return studentService.updateProfilePicture(SecurityService.getLoggedId(), file);
  }

  @PatchMapping("/me/setting")
  @RolesAllowed({Roles.STUDENT})
  public void updateSetting(@RequestBody StudentSettingsDTO dto) {
    studentService.updateSettings(dto);
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getPostsStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal TokenPayload token) {
    return postService.getAuthorPosts(id, page, token);
  }

  @GetMapping("/{id}/photo")
  @RolesAllowed({Roles.STUDENT})
  public Page<MatchedView> getPhotosStudent(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return mediaService.getPhotosTaggedByStudent(id, page);
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
    return factory.toAdminView(studentService.createStudent(dto));
  }

  @GetMapping("/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Page<StudentPreviewAdmin> getAllStudentsAdmin(@RequestParam(defaultValue = "0") int page) {
    return studentService.getAllStudent(page).map(factory::toPreviewAdmin);
  }

  @GetMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN})
  public StudentAdminView getStudentAdmin(@PathVariable Long id) {
    return factory.toAdminView(studentService.getStudent(id));
  }

  @PutMapping("/admin")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView updateStudentAdmin(@RequestBody StudentUpdateAdminDTO dto) {
    return factory.toAdminView(studentService.updateStudentAdmin(dto));
  }

  @PutMapping("/{id}/admin/picture")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentPictures updateAdminPicture(@PathVariable Long id, @RequestBody MultipartFile file) {
    return studentService.updateProfilePicture(id, file);
  }


  @DeleteMapping("/{id}/admin/picture/custom")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentPictures deleteCustomPicture(@PathVariable Long id) {
    return studentService.updateProfilePicture(id, null);
  }


  @PutMapping("/{id}/admin/picture/original")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentPictures updateOriginalPicture(@PathVariable Long id, @RequestBody MultipartFile file) {
    return studentService.updateOriginalPicture(id, file);
  }

  @PostMapping("/import")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView importStudent(@ModelAttribute Student student, @RequestParam(value = "file",
          required = false) MultipartFile file) {
    return factory.toAdminView(studentImportService.importStudents(student, file));
  }

  @PostMapping("/import/multiple")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView[] importStudents(
    @RequestParam(value = "firstName[]") String[] firstNames,
    @RequestParam(value = "lastName[]") String[] lastName,
    @RequestParam(value = "id[]") Long[] ids,
    @RequestParam(value = "promo[]") Integer[] promos,
    @RequestParam(value = "hasFile[]") boolean[] hasFiles,
    @RequestParam(value = "file[]", required = false) MultipartFile[] files) {

    int numStudent = ids.length;
    int fileIndex = 0;

    StudentAdminView[] studentAdminViews = new StudentAdminView[numStudent];

    for(int x = 0;x <numStudent;x++){
      Student student = new Student();
      student.setId(ids[x]);
      student.setFirstName(firstNames[x]);
      student.setLastName(lastName[x]);
      student.setPromo(promos[x]);
      student.setFeed(new Feed(student.getName()));

      studentAdminViews[x] = factory.toAdminView(studentImportService.importStudents(student, hasFiles[x] ? files[fileIndex] : null));

      if(hasFiles[x]){
        fileIndex++;
      }
    }

    return studentAdminViews;
  }

  @GetMapping("/promos")
  @RolesAllowed({Roles.STUDENT})
  public List<Integer> getAllPromos() {
    return studentService.getAllPromo();
  }
}
