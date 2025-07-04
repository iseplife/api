package com.iseplife.api.services;

import com.google.common.collect.Sets;
import com.iseplife.api.constants.FeedType;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.group.GroupMemberRepository;
import com.iseplife.api.dto.group.GroupMemberDTO;
import com.iseplife.api.entity.group.GroupMember;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.FamilyType;
import com.iseplife.api.constants.Language;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.ISEPCAS.CASUserDTO;
import com.iseplife.api.dto.student.StudentDTO;
import com.iseplife.api.dto.student.StudentSettingsDTO;
import com.iseplife.api.dto.student.StudentUpdateAdminDTO;
import com.iseplife.api.dto.student.view.StudentPictures;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.exceptions.http.HttpUnauthorizedException;
import com.iseplife.api.services.fileHandler.FileHandler;
import com.iseplife.api.utils.MediaUtils;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class StudentService {
  final private ModelMapper mapper;
  @Lazy
  final private SubscriptionService subscriptionService;
  final private StudentRepository studentRepository;
  final private GroupRepository groupRepository;
  final private GroupMemberRepository groupMemberRepository;
  final private RoleRepository roleRepository;
  final private ClubRepository clubRepository;
  @Lazy final private GroupService groupService;
  final private ClubService clubService;

  @Qualifier("FileHandlerBean")
  final private FileHandler fileHandler;

  final private static int RESULTS_PER_PAGE = 20;

  @PostConstruct
  private void init() {
    mapper.typeMap(StudentSettingsDTO.class, Student.class).addMappings(mapper ->
      mapper.using(ctx -> Language.valueOf(((String) ctx.getSource()).toUpperCase())).map(StudentSettingsDTO::getLanguage, Student::setLanguage)
    );
  }

  public Page<Student> getAllStudent(int page) {
    return studentRepository.findAllByOrderByLastName(PageRequest.of(page, RESULTS_PER_PAGE));
  }

  public Student getStudent(Long id) {
    Optional<Student> student = studentRepository.findById(id);
    if (student.isEmpty())
      throw new HttpNotFoundException("student_not_found");

    return student.get();
  }


  public List<Student> getStudents(List<Long> ids) {
    List<Student> students = (List<Student>) studentRepository.findAllById(ids);

    if (students.size() != ids.size())
      throw new HttpNotFoundException("students_not_found");

    return students;
  }


  public Student hydrateStudent(Student student, CASUserDTO user) {
    student.setId(user.getNumero());
    student.setFirstName(user.getPrenom());
    student.setLastName(user.getNom());
    student.setMail(user.getMail());

    String[] split = user.getTitre().split("-");
    Integer promo = null;
    for(int i = split.length - 1;i != 0;i--) {
      try {
        promo = Integer.valueOf(split[i]);
      }catch(Exception err) { }
    }
    if(promo == null)
      throw new HttpUnauthorizedException("error_moodle_acc");
    student.setPromo(Integer.valueOf(promo));

    student = studentRepository.save(student);

    boolean wasInPromo = groupService.isInPromoGroup(student);
    if(!wasInPromo)
      groupService.addToPromoGroup(student);

    groupService.addToNeurchiIfNotPresent(student);

    System.out.println("User "+student.getName()+" ("+student.getId()+") hydrated");

    return student;
  }

  public void updateSettings(StudentSettingsDTO settingDTO) {
    Student student = getStudent(SecurityService.getLoggedId());

    mapper.map(settingDTO, student);
    studentRepository.save(student);
  }

  private static FamilyType[] FAMILIES = {FamilyType.BLUE_LION, FamilyType.GREEN_GORILLA, FamilyType.RED_FOX, FamilyType.YELLOW_EAGLE};

  public Student createStudent(StudentDTO dto) {
    if (studentRepository.existsById(dto.getId())){
      Optional<Student> student= studentRepository.findById(dto.getId());

      if(student.isPresent()){
        groupService.addToNeurchiIfNotPresent(student.get());
        return student.get();
      } else {
        throw new HttpNotFoundException("Student not found");
      }

    } else {
      Student student = mapper.map(dto, Student.class);

      if(dto.getRoles() == null){
        student.setRoles(roleRepository.findAllByRoleIn(List.of(Roles.STUDENT)));
      } else {
        student.setRoles(roleRepository.findAllByRoleIn(dto.getRoles()));
      }

      student.setFeed(new Feed(student.getName(), FeedType.STUDENT));

      // random select
      student.setFamily(FAMILIES[(int) (Math.random() * FAMILIES.length)]);

      student = studentRepository.save(student);
      subscriptionService.subscribe(student, student, false);

      groupService.addToPromoGroup(student);
      groupService.addToNeurchiIfNotPresent(student);

      return student;
    }

  }

  public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
  }


  public StudentPictures updateOriginalPicture(Long studentId, MultipartFile image) throws IOException {
    Student student = getStudent(studentId);
    String picture = student.getPicture();

    if (image == null && picture != null) {
      fileHandler.delete(
        StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + MediaUtils.extractFilename(picture)
      );

      if (MediaUtils.isOriginalPicture(student.getPicture()))
        student.setPicture(null);
    } else if (image != null) {
      // We don't have to delete previous picture has the name won't change then the new pictures will override the old one
      String newPicture = uploadOriginalPicture(student.getPicture(), image);
      if (picture == null)
        student.setPicture(newPicture);
      student.setHasDefaultPicture(true);
    }
    studentRepository.save(student);

    String filename = MediaUtils.extractFilename(student.getPicture());
    return image == null ?
      new StudentPictures(
        null,
        student.getPicture()
      ) :
      new StudentPictures(
        StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + filename,
        StorageConfig.MEDIAS_CONF.get("user_avatar").path + "/" + filename
      );
  }

  public StudentPictures updateProfilePicture(Long studentId, MultipartFile image) throws IOException {
    Student student = getStudent(studentId);
    if (image == null) {
      fileHandler.delete(student.getPicture());

      // If student has default picture set it back otherwise picture is null
      student.setPicture(
        student.getHasDefaultPicture() ?
          StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + MediaUtils.extractFilename(student.getPicture()) :
          null
      );
    } else {
      // We don't have to delete previous picture has the name won't change then the new pictures will override the old one
      student.setPicture(
        uploadPicture(student.getPicture(), image)
      );
    }
    studentRepository.save(student);

    return image == null ?
      new StudentPictures(
        student.getPicture(),
        null
      ) :
      new StudentPictures(
        student.getHasDefaultPicture() ?
          StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + MediaUtils.extractFilename(student.getPicture()) :
          null,
        student.getPicture()
      );
  }

  public String uploadOriginalPicture(String previousPicture, MultipartFile image) throws IOException {
    Map params = Map.of(
      "process", "compress",
      "sizes", StorageConfig.MEDIAS_CONF.get("user_original").sizes,
      "dest_ext", "jpeg"
    );
    return previousPicture == null ?
      fileHandler.upload(
        image,
        StorageConfig.MEDIAS_CONF.get("user_original").path,
        false,
        params
      ) :
      fileHandler.upload(
        image,
        StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + MediaUtils.extractFilename(previousPicture),
        true,
        params
      );
  }

  private String uploadPicture(String previousPicture, MultipartFile image) throws IOException {
    Map params = Map.of(
      "process", "compress",
      "sizes", StorageConfig.MEDIAS_CONF.get("user_avatar").sizes,
      "dest_ext", "jpeg"
    );

    // If picture is undefined then the unique picture identifier has never been generated (or lost) and need to be created
    return previousPicture == null ?
      fileHandler.upload(
        image,
        StorageConfig.MEDIAS_CONF.get("user_avatar").path,
        false,
        params
      ) :
      fileHandler.upload(
        image,
        StorageConfig.MEDIAS_CONF.get("user_avatar").path + "/" + MediaUtils.extractFilename(previousPicture),
        true,
        params
      );

  }

  public Boolean toggleArchiveStudent(Long id) {
    Student student = getStudent(id);

    student.setArchivedAt(student.isArchived() ? null : new Date());
    studentRepository.save(student);

    return student.isArchived();
  }

  public Role getRole(String role) {
    return roleRepository.findByRole(role);
  }

  public void toggleNotifications(TokenPayload tokenPayload) {
    Student student = getStudent(tokenPayload.getId());
    student.setNotification(!student.getNotification());
    studentRepository.save(student);
  }

  public Set<Role> getStudentRoles(Long id) {
    Student student = getStudent(id);
    return student.getRoles();
  }

  public List<Club> getPublisherClubs(Student student) {
    return clubService.getUserCurrentClubsWith(student, ClubRole.PUBLISHER);
  }

  public List<Role> getRoles() {
    return roleRepository.findAll();
  }

  public Student updateStudentAdmin(StudentUpdateAdminDTO dto) {
    Student student = getStudent(dto.getId());
    mapper.map(dto, student);

    Set<Role> roles = roleRepository.findAllByRoleIn(dto.getRoles());
    student.setRoles(roles);

    return studentRepository.save(student);
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

  public Student didFirstFollow(Student student) {
    student.setDidFirstFollow(true);
    return studentRepository.save(student);
  }

  public void updateLastExplore(Date date) {
    studentRepository.updateLastExplore(SecurityService.getLoggedId(), date);
  }
  
  public void updatePassword(Long studentId, String password) {
    String bcryptHashString = BCrypt.hashpw(password, BCrypt.gensalt());
    studentRepository.updatePassword(studentId, bcryptHashString);
  }
}
