package com.iseplive.api.services;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.ClubRoles;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dao.club.ClubFactory;
import com.iseplive.api.dao.club.ClubMemberRepository;
import com.iseplive.api.dao.club.ClubRepository;
import com.iseplive.api.dao.club.ClubRoleRepository;
import com.iseplive.api.dao.post.AuthorRepository;
import com.iseplive.api.dto.ClubDTO;
import com.iseplive.api.dto.view.ClubMemberView;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.club.ClubMember;
import com.iseplive.api.entity.club.ClubRole;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Service
public class ClubService {

  @Autowired
  AuthorRepository authorRepository;

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  ClubRoleRepository clubRoleRepository;

  @Autowired
  ClubMemberRepository clubMemberRepository;

  @Autowired
  ClubFactory clubFactory;

  @Autowired
  StudentService studentService;

  @Autowired
  MediaUtils imageUtils;

  @Value("${storage.club.url}")
  public String clubLogoStorage;

  private static final int WIDTH_LOGO_CLUB = 256;

  public Club createClub(ClubDTO dto, MultipartFile logo) {
    Club club = clubFactory.dtoToEntity(dto);
    if (dto.getAdminId() == null) {
      throw new IllegalArgumentException("The id of the admin cannot be null");
    }
    Student admin = studentService.getStudent(dto.getAdminId());
    if (admin == null) {
      throw new IllegalArgumentException("this student id doesn't exist");
    }
    club.setAdmins(Collections.singleton(admin));

    ClubMember clubMember = new ClubMember();
    clubMember.setMember(admin);
    clubMember.setRole(getClubRole(ClubRoles.PRESIDENT));
    clubMember.setClub(club);

    club.setMembers(Collections.singletonList(clubMember));
    setClubLogo(club, logo);
    return authorRepository.save(club);
  }

  public List<Club> searchClubs(String name) {
    return clubRepository.findAllByNameContainingIgnoringCase(name);
  }

  private ClubRole getClubRole(String role) {
    ClubRole clubRole = clubRoleRepository.findOneByName(role);
    if (clubRole == null) {
      throw new IllegalArgumentException("Could not find this role: "+role);
    }
    return clubRole;
  }

  private void setClubLogo(Club club, MultipartFile file) {
    String path = imageUtils.resolvePath(clubLogoStorage, club.getName(), false);
    imageUtils.removeIfExistJPEG(path);
    imageUtils.saveJPG(file, WIDTH_LOGO_CLUB, path);
    club.setLogoUrl(imageUtils.getPublicUrlImage(path));
  }

  public ClubMember addMember(Long clubId, Long studentId) {
    clubMemberRepository.findByClubId(clubId).forEach(club -> {
      if (club.getMember().getId().equals(studentId)) {
        throw new IllegalArgumentException("this student is already part of this club");
      }
    });

    ClubMember clubMember = new ClubMember();
    clubMember.setClub(getClub(clubId));
    clubMember.setMember(studentService.getStudent(studentId));
    clubMember.setRole(getClubRole(ClubRoles.MEMBER));

    return clubMemberRepository.save(clubMember);
  }

  private ClubRole getClubRole(Long id) {
    return clubRoleRepository.findOne(id);
  }

  public List<Club> getAll() {
    return clubRepository.findAllByOrderByName();
  }

  public Club getClub(Long id) {
    Club club = clubRepository.findOne(id);
    if (club == null) throw new IllegalArgumentException("could not find club with id: " + id);
    return club;
  }

  /**
   * Retrieve the list of clubs where the student is admin
   *
   * @param student student id
   * @return a club list
   */
  List<Club> getClubAuthors(Student student) {
    return clubRepository.findByAdminsContains(student);
  }

  public void deleteClub(Long id) {
    clubRepository.delete(id);
  }

  public List<ClubMember> getMembers(Long id) {
    return clubMemberRepository.findByClubId(id);
  }

  public ClubRole createRole(String role, Long clubId) {
    Club club = getClub(clubId);
    ClubRole clubRole = new ClubRole();
    clubRole.setName(role);
    clubRole.setClub(club);
    return clubRoleRepository.save(clubRole);
  }

  public Set<Student> getAdmins(Long clubId) {
    Club club = getClub(clubId);
    return club.getAdmins();
  }

  public void addAdmin(Long clubId, Long studId) {
    Student student = studentService.getStudent(studId);
    Club club = getClub(clubId);
    club.getAdmins().add(student);
    clubRepository.save(club);
  }

  public void removeAdmin(Long clubId, Long studId) {
    Club club = getClub(clubId);
    Student student = studentService.getStudent(studId);
    club.getAdmins().remove(student);
    clubRepository.save(club);
  }

  public List<Club> getAdminClubs(Student student) {
    return clubRepository.findByAdminsContains(student);
  }

  public Club updateClub(Long id, ClubDTO clubDTO, MultipartFile logo) {
    Club club = getClub(id);

    club.setName(clubDTO.getName());
    club.setCreation(clubDTO.getCreation());
    club.setDescription(clubDTO.getDescription());
    club.setWebsite(clubDTO.getWebsite());

    if (logo != null) {
      setClubLogo(club, logo);
    }
    return clubRepository.save(club);
  }

  public ClubMember updateMemberRole(Long member, Long role, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    if (!payload.getRoles().contains(Roles.ADMIN) && payload.getRoles().contains(Roles.CLUB_MANAGER)) {
      if (!payload.getClubsAdmin().contains(clubMember.getClub().getId())) {
        throw new AuthException("no rights to modify this club");
      }
    }
    clubMember.setRole(getClubRole(role));
    return clubMemberRepository.save(clubMember);
  }

  private ClubMember getMember(Long member) {
    ClubMember clubMember = clubMemberRepository.findOne(member);
    if (clubMember == null) {
      throw new IllegalArgumentException("member could not be found");
    }
    return clubMember;
  }

  public void removeMember(Long member, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    Club club = clubMember.getClub();
    if (!payload.getRoles().contains(Roles.ADMIN) && payload.getRoles().contains(Roles.CLUB_MANAGER)) {
      if (!payload.getClubsAdmin().contains(club.getId())) {
        throw new AuthException("no rights to modify this club");
      }
    }
    club.getAdmins().remove(clubMember.getMember());
    club.getMembers().remove(clubMember);
    clubRepository.save(club);
  }

  public List<ClubMemberView> getStudentClubs(Long id) {
    List<ClubMember> clubMembers = clubMemberRepository.findByMember_Id(id);
    return clubMembers.stream().map(cm -> {
      ClubMemberView clubMemberView = new ClubMemberView();
      clubMemberView.setClub(cm.getClub());
      clubMemberView.setMember(cm.getMember());
      clubMemberView.setRole(cm.getRole());
      return clubMemberView;
    }).collect(Collectors.toList());
  }

  public Club getIsepLive() {
    return clubRepository.findByIsAdmin(true);
  }

  public List<ClubRole> getClubRoles(Long id) {
    return clubRoleRepository.findByClub_Id(id);
  }

  public void deleteClubRole(Long roleid) {
    ClubRole role = clubRoleRepository.findOne(roleid);
    if (role != null) {
      clubRoleRepository.delete(role);
    }
  }
}
