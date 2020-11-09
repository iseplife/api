package com.iseplife.api.services;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.club.ClubMemberRepository;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dto.CASUserDTO;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.StudentUpdateDTO;
import com.iseplife.api.dto.student.view.StudentAdminView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.student.view.StudentPreviewAdmin;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class StudentService {

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  StudentFactory studentFactory;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ClubMemberRepository clubMemberRepository;

  @Autowired
  ClubRepository clubRepository;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  private static final int RESULTS_PER_PAGE = 20;

  private Page<Student> getAllStudent(int page) {
    return studentRepository.findAllByOrderByLastName(PageRequest.of(page, RESULTS_PER_PAGE));
  }

  public Page<StudentPreview> getAll(int page) {
    return getAllStudent(page).map(StudentFactory::toPreview);
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

  public List<Student> getStudents(List<Long> ids) {
    List<Student> students = (List<Student>) studentRepository.findAllById(ids);

    if (students.size() != ids.size())
      throw new IllegalArgumentException("could not find one of the user");

    return students;
  }

  public void hydrateStudent(Student student, CASUserDTO user){
    student.setFirstName(user.getPrenom());
    student.setLastName(user.getNom());
    student.setMail(user.getMail());

    String[] titre = user.getTitre().split("-");
    student.setPromo(Integer.valueOf(titre[2]));

    if(student.getRoles().size() == 0)
      student.setRoles(Collections.singleton(roleRepository.findByRole(Roles.STUDENT)));

    studentRepository.save(student);
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

    Map params = Map.of(
      "process", "resize",
      "sizes", StorageConfig.MEDIAS_CONF.get("user_original")
    );
    return fileHandler.upload(image, StorageConfig.MEDIAS_CONF.get("user_original") + "/" + name, true, params);
  }

  private void updateProfilePicture(Student student, MultipartFile image) {
    Map params = Map.of(
      "process", "resize",
      "sizes", StorageConfig.MEDIAS_CONF.get("user_avatar").sizes
    );

    student.setPicture(
      fileHandler.upload(image, StorageConfig.MEDIAS_CONF.get("user_avatar").path, false, params)
    );
    studentRepository.save(student);
  }

  public Boolean toggleArchiveStudent(Long id) {
    Student student = getStudent(id);

    student.setArchivedAt(student.isArchived() ? null : new Date());
    studentRepository.save(student);

    return student.isArchived();
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


  public List<Integer> getAllPromo() {
    return studentRepository.findDistinctPromo();
  }

  public List<Feed> getFeeds(Student student) {
    List<Feed> feeds =
      clubRepository.findAllStudentClub(student)
        .stream()
        .map(Club::getFeed)
        .collect(Collectors.toList());
    List<Feed> groups = groupRepository.findAllUserGroups(student.getId())
      .stream()
      .map(Group::getFeed)
      .collect(Collectors.toList());

    feeds.addAll(groups);
    return feeds;
  }
}
