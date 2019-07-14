package com.iseplive.api.services;

import com.google.common.collect.Sets;
import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.dao.student.RoleRepository;
import com.iseplive.api.dao.student.StudentFactory;
import com.iseplive.api.dao.student.StudentRepository;
import com.iseplive.api.dto.student.StudentDTO;
import com.iseplive.api.dto.student.StudentUpdateAdminDTO;
import com.iseplive.api.dto.student.StudentUpdateDTO;
import com.iseplive.api.dto.view.StudentWithRoleView;
import com.iseplive.api.entity.user.Role;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
  MediaUtils imageUtils;

  @Autowired
  StudentFactory studentFactory;

  @Autowired
  RoleRepository roleRepository;

  @Value("${storage.student.url}")
  String studentImageStorage;

  private static final int RESULTS_PER_PAGE = 20;

  private static final int WIDTH_PROFILE_IMAGE = 768;
  private static final int WIDTH_PROFILE_IMAGE_THUMB = 384;


  public Page<Student> getAll(int page) {
    return studentRepository.findAll(new PageRequest(page, RESULTS_PER_PAGE));
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

  public Page<Student> search(String name, String promos, String sort, int page) {
    Sort.Direction direction = sort.equals("a") ? Sort.Direction.ASC : Sort.Direction.DESC;
    PageRequest pageRequest = new PageRequest(
      page,
      RESULTS_PER_PAGE,
      new Sort(
        new Sort.Order(Sort.Direction.DESC, "promo"),
        new Sort.Order(direction, "lastname")
      )
    );
    if (!promos.isEmpty()) {
      List<Integer> promoInt = Arrays.stream(promos.split(","))
        .map(Integer::decode)
        .collect(Collectors.toList());
      return studentRepository.searchStudent(
        name.toLowerCase(),
        promoInt,
        pageRequest
      );
    }
    return studentRepository.searchStudent(
      name.toLowerCase(),
      pageRequest
    );
  }

  public Page<StudentWithRoleView> searchAdmin(String name, String roles, String promos, String sort, int page) {
    PageRequest pageRequest = new PageRequest(
      page,
      RESULTS_PER_PAGE,
      new Sort(
        new Sort.Order(Sort.Direction.DESC, "promo")
      )
    );

    if (!roles.isEmpty()) {
      Set<Role> rolesList = Sets.newHashSet(roles.split(",")).stream()
        .map(r -> roleRepository.findByRole(r))
        .collect(Collectors.toSet());

      if (promos.isEmpty()) {
        return studentRepository.searchStudentRole(name, rolesList, pageRequest)
          .map(s -> studentFactory.studentToStudentWithRoles(s));
      } else {
        List<Integer> promoInt = Arrays.stream(promos.split(","))
          .map(Integer::decode)
          .collect(Collectors.toList());
        return studentRepository.searchStudentRolePromo(name, rolesList, promoInt, pageRequest)
          .map(s -> studentFactory.studentToStudentWithRoles(s));
      }
    }

    return search(name, promos, sort, page)
      .map(s -> studentFactory.studentToStudentWithRoles(s));
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
    String path = imageUtils.resolvePath(studentImageStorage,
      Long.toString(student.getId()), false, student.getId());
    imageUtils.removeIfExistJPEG(path);
    imageUtils.saveJPG(image, WIDTH_PROFILE_IMAGE, path);

    String pathThumb = imageUtils.resolvePath(studentImageStorage,
      Long.toString(student.getId()), true, student.getId());
    imageUtils.removeIfExistJPEG(pathThumb);
    imageUtils.saveJPG(image, WIDTH_PROFILE_IMAGE_THUMB, pathThumb);

    student.setPhotoUrl(imageUtils.getPublicUrlImage(path));
    student.setPhotoUrlThumb(imageUtils.getPublicUrlImage(pathThumb));
  }

  public Page<StudentWithRoleView> getAllForAdmin(int page) {
    return getAll(page).map(s -> studentFactory.studentToStudentWithRoles(s));
  }
}
