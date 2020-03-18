package com.iseplife.api.services;

import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.FeedConstant;
import com.iseplife.api.dao.club.ClubMemberRepository;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.view.StudentWithRoleView;
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

  @Value("${storage.student.url}")
  String studentImageStorage;

  private static final int RESULTS_PER_PAGE = 20;

  private static final int WIDTH_PROFILE_IMAGE = 768;
  private static final int WIDTH_PROFILE_IMAGE_THUMB = 384;


  public Page<Student> getAll(int page) {
    return studentRepository.findAllByOrderByLastName(new PageRequest(page, RESULTS_PER_PAGE));
  }

  public Student getStudent(Long id) {
    Student student = studentRepository.findOne(id);
    if (student != null) {
      return student;
    }
    throw new IllegalArgumentException("could not find the student with id: " + id);
  }

  public Student createStudent(StudentDTO dto) {
    Student student = studentFactory.dtoToEntity(dto);
    return studentRepository.save(student);
  }

  void addProfileImage(Long studentId, MultipartFile image) {
    Student student = studentRepository.findOne(studentId);
    updateProfileImage(student, image);
    studentRepository.save(student);
  }

  public void toggleArchiveStudent(Long id) {
    Student student = getStudent(id);
    student.setArchivedAt(new Date());
    studentRepository.save(student);
  }

  public Student updateStudent(StudentUpdateDTO dto, Long id) {
    Student student = studentRepository.findOne(id);
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

  public Student updateStudentAdmin(StudentUpdateAdminDTO dto, MultipartFile file) {
    Student student = getStudent(dto.getId());
    studentFactory.updateAdminDtoToEntity(student, dto);

    if (file != null) {
      updateProfileImage(student, file);
    }

    Set<Role> roles = roleRepository.findAllByIdIn(dto.getRoles());
    student.setRoles(roles);

    return studentRepository.save(student);
  }

  private void updateProfileImage(Student student, MultipartFile image) {
    fileHandler.upload(image, "user/", Collections.EMPTY_MAP);
  }

  public Page<StudentWithRoleView> getAllForAdmin(int page) {
    return getAll(page).map(s -> studentFactory.studentToStudentWithRoles(s));
  }

  public List<String> getAllPromo() {
    return studentRepository.findDistinctPromo();
  }
}
