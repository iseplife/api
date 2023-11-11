package com.iseplife.api.controllers;

import java.util.*;

import javax.annotation.security.RolesAllowed;

import com.iseplife.api.constants.FamilyType;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.club.projection.ClubMemberStudentProjection;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentSettingsDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.view.LoggedStudentPreview;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentOverview;
import com.iseplife.api.dto.student.view.StudentPictures;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.dto.student.view.StudentView;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.services.ClubService;
import com.iseplife.api.services.MediaService;
import com.iseplife.api.services.NotificationService;
import com.iseplife.api.services.PostService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;
import com.iseplife.api.services.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
  final private PostService postService;
  final private MediaService mediaService;
  final private StudentService studentService;
  final private ClubService clubService;
  final private NotificationService notificationService;
  final private StudentFactory factory;
  final private SubscriptionService subscriptionService;

  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public LoggedStudentPreview getLoggedStudentPreview() {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    return factory.toSelfPreview(student, notificationService.countUnwatchedAndAllByStudents(student));
  }

  @GetMapping("/me/full")
  @RolesAllowed({Roles.STUDENT})
  public StudentView getLoggedStudent(@AuthenticationPrincipal TokenPayload token) {
    return factory.toView(studentService.getStudent(token.getId()));
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public StudentOverview getStudent(@PathVariable Long id) {
    Student student = studentService.getStudent(id);
    return factory.toOverview(student, subscriptionService.getSubscriptionProjection(student));
  }

  @PutMapping("/me/did-first-follow")
  @RolesAllowed({Roles.STUDENT})
  public LoggedStudentPreview didFirstFollow() {
    Student student = studentService.didFirstFollow(studentService.getStudent(SecurityService.getLoggedId()));
    return factory.toSelfPreview(student, notificationService.countUnwatchedAndAllByStudents(student));
  }

  @PostMapping("/me/picture")
  @RolesAllowed({Roles.STUDENT})
  public StudentPictures updatePicture(@RequestBody MultipartFile file) {
    return studentService.updateProfilePicture(SecurityService.getLoggedId(), file);
  }

  @PostMapping("/me/last-explore")
  @RolesAllowed({Roles.STUDENT})
  public Long updateLastExplore() {
    Date date = new Date();
    studentService.updateLastExplore(date);
    return date.getTime();
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

  @GetMapping("/{id}/clubs")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubMemberStudentProjection> getStudentClubs(@PathVariable Long id) {
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
  public StudentAdminView createStudent(@RequestBody StudentDTO dto) {
    return factory.toAdminView(studentService.createStudent(dto));
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public void deleteStudent(@PathVariable Long id) {
    if(id == 1)
      throw new HttpBadRequestException("Can't delete admin user");
    studentService.deleteStudent(id);
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
  public StudentAdminView importStudent(@ModelAttribute StudentDTO student, @RequestParam(value = "file",
          required = false) MultipartFile file) {
    return factory.toAdminView(studentService.createStudent(student));
  }

  @PostMapping("/import/multiple")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public StudentAdminView[] importStudents (
    @RequestParam(value = "id[]") String[] id,
    @RequestParam(value = "firstName[]") String[] firstName,
    @RequestParam(value = "lastName[]") String[] lastName,
    @RequestParam(value = "promo[]") String[] promo) {

    StudentAdminView[] studentAdminViews = new StudentAdminView[id.length];

    for(int x = 0;x <id.length;x++){
      StudentDTO dto = StudentDTO.builder().id(Long.valueOf(id[x]))
          .lastName(lastName[x])
            .firstName(firstName[x])
              .promo(Integer.valueOf(promo[x])).build();

      studentAdminViews[x] = factory.toAdminView(studentService.createStudent(dto));
    }

    return studentAdminViews;
  }

  @PostMapping("/import/family")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public int importStudentsFamily (
    @RequestParam(value = "id[]") String[] id,
    @RequestParam(value = "family[]") String[] family) {

    System.out.println("import family");

    for(int x = 0;x <id.length;x++){

      System.out.println(id.length);

      FamilyType familyType = FamilyType.valueOf(family[x]);
      System.out.println(familyType);

      studentService.setFamily(Long.valueOf(id[x]), familyType);
    }

    return id.length;
  }

  @PostMapping("/import/multiple/pictures")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER})
  public Integer importStudentsPictures(
    @RequestParam(value = "files[]", required = false) MultipartFile[] files) {

    for (MultipartFile file : files) {
        try {
          Long studentId = Long.valueOf(Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0]);
          studentService.updateOriginalPicture(studentId, file);
        } catch (Exception e){
          System.out.println("Failed to import " + file.getOriginalFilename() + ": " + e.getMessage());
        }

    }

    return files.length;
  }

  @GetMapping("/promos")
  @RolesAllowed({Roles.STUDENT})
  public List<Integer> getAllPromos() {
    return studentService.getAllPromo();
  }
}
