package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.FeedConstant;
import com.iseplife.api.dao.club.ClubMemberRepository;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Service
public class StudentService {

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  StudentFactory studentFactory;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  FeedRepository feedRepository;

  @Autowired
  ClubMemberRepository clubMemberRepository;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  @Value("${storage.image.student.default}")
  protected String defaultPath;

  @Value("${storage.image.student.original}")
  protected String originalPath;

  private static final int RESULTS_PER_PAGE = 20;

  public String getSigned(){
    return fileHandler.generatePresignUrl("post/a2C9IyruWsHogCCUWIJzrufereJKu5.jpg");
  }

  private Page<Student> getAllStudent(int page) {
    return studentRepository.findAllByOrderByLastName(PageRequest.of(page, RESULTS_PER_PAGE));
  }

  public Page<StudentPreview> getAll(int page) {
    return getAllStudent(page).map(StudentFactory::entityToPreview);
  }

  public Page<StudentPreviewAdmin> getAllForAdmin(int page) {
    return getAllStudent(page).map(StudentFactory::entityToPreviewAdmin);
  }

  public Student getStudent(Long id) {
    Optional<Student> student = studentRepository.findById(id);
    if (student.isEmpty())
      throw new IllegalArgumentException("could not find the student with id: " + id);

    return student.get();
  }

  public StudentAdminView createStudent(StudentDTO dto, MultipartFile file) {
    if (studentRepository.existsById(dto.getId()))
      throw new IllegalArgumentException("Student already exist with this id (" + dto.getId() + ")");

    Student student = studentFactory.dtoToEntity(dto);
    student.setRoles(roleRepository.findAllByRoleIn(dto.getRoles()));

    if (file != null)
      student.setPicture(uploadOriginalPicture(student, file));

    return StudentFactory.entityToAdminView(
      studentRepository.save(student)
    );
  }

  void addProfilePicture(Long studentId, MultipartFile image) {
    Student student = getStudent(studentId);
    updateProfilePicture(student, image);
    studentRepository.save(student);
  }

  public String uploadOriginalPicture(Student student, MultipartFile image) {
    String name = student.getId() + "." + fileHandler.getFileExtension(image.getName());

    return fileHandler.upload(image, originalPath + "/" + name, true);
  }

  public void updateProfilePicture(Long id, MultipartFile image) {
    updateProfilePicture(getStudent(id), image);
  }

  private void updateProfilePicture(Student student, MultipartFile image) {
    student.setPicture(
      fileHandler.upload(image, defaultPath, false)
    );
    studentRepository.save(student);
  }

  public StudentAdminView toggleArchiveStudent(Long id) {
    Student student = getStudent(id);

    student.setArchivedAt(student.isArchived() ? null : new Date());
    return StudentFactory.entityToAdminView(studentRepository.save(student));
  }

  public Student updateStudent(StudentUpdateDTO dto, Long id) {
    Student student = getStudent(id);
    studentFactory.updateDtoToEntity(student, dto);
    return studentRepository.save(student);
  }

  public Role getRole(String role) {
    return roleRepository.findByRole(role);
  }

  public void toggleNotifications(TokenPayload tokenPayload) {
    Student student = getStudent(tokenPayload.getId());
    student.setAllowNotifications(!student.getAllowNotifications());
    studentRepository.save(student);
  }

  public Set<Role> getStudentRoles(Long id) {
    Student student = getStudent(id);
    return student.getRoles();
  }

  public List<Club> getPublisherClubs(Student student) {
    return clubMemberRepository.findByRoleWithInheritance(student, ClubRole.PUBLISHER);
  }


  public List<Feed> getFeeds(Student student, List<String> roles, List<Long> adminClub) {
    List<Feed> feeds =
      clubMemberRepository.findByStudentId(student.getId())
        .stream()
        .map(cm -> cm.getClub().getFeed())
        .collect(Collectors.toList());

    if (roles.contains("ROLE_ADMIN")) {
      feeds.addAll(feedRepository.findAll());
    } else {
      List<String> names = new ArrayList<>();
      names.add("PROMO_" + student.getPromo());
      if (feeds.size() > 0)
        names.add(FeedConstant.ASSOCIATION_LIFE.name());
      if (adminClub.size() > 0) {
        names.add(FeedConstant.ADMIN_ASSOCIATION.name());
      }
      feeds.addAll(feedRepository.findAllByNameIn(names));
    }
    return feeds;
  }

  public List<Role> getRoles() {
    return roleRepository.findAll();
  }

  public StudentAdminView updateStudentAdmin(StudentUpdateAdminDTO dto, MultipartFile file) {
    Student student = getStudent(dto.getId());
    studentFactory.updateAdminDtoToEntity(student, dto);

    if (file != null) {
      updateProfilePicture(student, file);
    } else if (dto.getResetPicture()) {
      fileHandler.delete(student.getPicture());
    }

    Set<Role> roles = roleRepository.findAllByRoleIn(dto.getRoles());
    student.setRoles(roles);

    return StudentFactory.entityToAdminView(studentRepository.save(student));
  }


  public List<String> getAllPromo() {
    return studentRepository.findDistinctPromo();
  }
}
