package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubType;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.club.ClubDTO;
import com.iseplife.api.dto.club.view.ClubMemberView;
import com.iseplife.api.dto.club.view.ClubView;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.club.ClubMemberRepository;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Service
public class ClubService {

  @Autowired
  AuthService authService;

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  ClubMemberRepository clubMemberRepository;

  @Autowired
  ClubFactory clubFactory;

  @Autowired
  StudentService studentService;

  @Autowired
  GalleryService galleryService;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  public Club getClub(Long id) {
    Optional<Club> club = clubRepository.findById(id);
    if (club.isEmpty())
      throw new IllegalArgumentException("could not find club with id: " + id);

    return club.get();
  }

  public ClubView createClub(ClubDTO dto) {
    Club club = ClubFactory.fromDTO(dto, new Club());
    if (dto.getAdmins().size() == 0)
      throw new IllegalArgumentException("The id of the admin cannot be null");

    List<Student> admins = studentService.getStudents(dto.getAdmins());
    List<ClubMember> members = new ArrayList<>();
    admins.forEach(a -> {
      ClubMember member = new ClubMember();
      member.setStudent(a);
      member.setRole(ClubRole.SUPER_ADMIN);
      member.setClub(club);

      members.add(member);
    });
    club.setMembers(members);
    club.setFeed(new Feed());

    return ClubFactory.toView(clubRepository.save(club));
  }

  public ClubView updateClub(Long id, ClubDTO dto) {
    Club club = getClub(id);
    if(!authService.hasRightOn(club))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")");

    // Update through reference so we don't need to get return value
    ClubFactory.fromDTO(dto, club);

    return ClubFactory.toView(clubRepository.save(club));
  }


  public String updateLogo(Long id, MultipartFile file) {
    Club club = getClub(id);

    if(club.getLogoUrl() != null)
      fileHandler.delete(club.getLogoUrl());

    club.setLogoUrl(fileHandler.upload(file, "/img/usr", false));
    clubRepository.save(club);
    return club.getLogoUrl();
  }

  public ClubMember addMember(Long clubId, Long studentId) {
    clubMemberRepository.findByClubId(clubId).forEach(member -> {
      if (member.getStudent().getId().equals(studentId)) {
        throw new IllegalArgumentException("this student is already part of this club");
      }
    });

    ClubMember clubMember = new ClubMember();
    clubMember.setClub(getClub(clubId));
    clubMember.setStudent(studentService.getStudent(studentId));
    clubMember.setRole(ClubRole.MEMBER);

    return clubMemberRepository.save(clubMember);
  }

  public List<Club> getAll() {
    return clubRepository.findAllByOrderByName();
  }



  public List<Student> getClubPublishers(Club club) {
    return clubMemberRepository.findClubPublishers(club, ClubRole.PUBLISHER);
  }

  public List<Club> getUserClubsWith(Student student, ClubRole role) {
    return clubRepository.findByRoleWithInheritance(student, role);
  }


  public Boolean toggleArchiveStatus(Long id) {
    Club club = getClub(id);
    club.setArchivedAt(club.isArchived() ? null : new Date());

    clubRepository.save(club);
    return club.isArchived();
  }

  public void deleteClub(Long id) {
    clubRepository.deleteById(id);
  }

  public List<ClubMember> getMembers(Long id) {
    return clubMemberRepository.findByClubId(id);
  }

  public Page<Gallery> getClubGalleries(Long id, int page) {
    return galleryService.getClubGalleries(getClub(id), page);
  }

  public Set<StudentPreview> getAdmins(Long clubId) {
    Club club = getClub(clubId);
    return club.getMembers()
      .stream()
      .filter(s -> s.getRole().is(ClubRole.ADMIN))
      .map(m -> StudentFactory.toPreview(m.getStudent()))
      .collect(Collectors.toSet());
  }

  public Set<Student> getAdmins(Club club) {
    return club.getMembers()
      .stream()
      .filter(s -> s.getRole().is(ClubRole.ADMIN))
      .map(ClubMember::getStudent)
      .collect(Collectors.toSet());
  }

  public void addAdmin(Long clubId, Long studId) {
    ClubMember member = clubMemberRepository.findOneByStudentIdAndClubId(studId, clubId);
    if (member == null)
      throw new IllegalArgumentException("the student needs to be part of the club to be an admin");

    member.setRole(ClubRole.ADMIN);
    clubRepository.save(member.getClub());
  }


  public void removeAdmin(Long clubId, Long studId) {
    ClubMember member = clubMemberRepository.findOneByStudentIdAndClubId(clubId, studId);
    member.setRole(ClubRole.MEMBER);

    clubMemberRepository.save(member);
  }

  public ClubMember updateMemberRole(Long member, ClubRole role, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      if (!payload.getClubsAdmin().contains(clubMember.getClub().getId())) {
        throw new AuthException("no rights to modify this club");
      }
    }
    clubMember.setRole(role);
    return clubMemberRepository.save(clubMember);
  }

  private ClubMember getMember(Long member) {
    Optional<ClubMember> clubMember = clubMemberRepository.findById(member);
    if (clubMember.isEmpty()) {
      throw new IllegalArgumentException("member could not be found");
    }
    return clubMember.get();
  }

  public void removeMember(Long member, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    Club club = clubMember.getClub();
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      if (!payload.getClubsAdmin().contains(club.getId())) {
        throw new AuthException("no rights to modify this club");
      }
    }
    clubMemberRepository.delete(clubMember);
    clubRepository.save(club);
  }

  public List<ClubMemberView> getStudentClubs(Long id) {
    List<ClubMember> clubMembers = clubMemberRepository.findByStudentId(id);
    return clubMembers.stream().map(cm -> {
      ClubMemberView clubMemberView = new ClubMemberView();
      clubMemberView.setClub(cm.getClub());
      clubMemberView.setMember(cm.getStudent());
      clubMemberView.setRole(cm.getRole());
      return clubMemberView;
    }).collect(Collectors.toList());
  }

}
