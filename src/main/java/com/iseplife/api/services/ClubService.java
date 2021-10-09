package com.iseplife.api.services;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.club.ClubMemberFactory;
import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dto.club.ClubAdminDTO;
import com.iseplife.api.dto.club.ClubDTO;
import com.iseplife.api.dto.club.ClubMemberCreationDTO;
import com.iseplife.api.dto.club.ClubMemberDTO;
import com.iseplife.api.dto.club.view.ClubMemberPreview;
import com.iseplife.api.dto.club.view.ClubView;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
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
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Service
public class ClubService {

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  ClubMemberRepository clubMemberRepository;

  @Autowired
  ClubFactory clubFactory;

  @Autowired
  StudentService studentService;

  @Autowired
  FeedService feedService;

  @Autowired
  GalleryService galleryService;

  @Qualifier("FileHandlerBean" )
  @Autowired
  FileHandler fileHandler;

  @Cacheable("club" )
  public Club getClub(Long id) {
    Optional<Club> club = clubRepository.findById(id);
    if (club.isEmpty())
      throw new IllegalArgumentException("could not find club with id: " + id);

    return club.get();
  }

  public static Integer getCurrentSchoolYear() {
    Calendar c = Calendar.getInstance();
    return c.get(Calendar.MONTH) >= Calendar.SEPTEMBER ?
      c.get(Calendar.YEAR) :
      c.get(Calendar.YEAR) - 1;
  }

  public ClubView getClubView(Long id) {
    Club club = getClub(id);

    return ClubFactory.toView(
      club,
      SecurityService.hasRightOn(club),
      feedService.isSubscribedToFeed(club)
    );
  }


  public ClubView createClub(ClubAdminDTO dto) {
    Club club = ClubFactory.fromAdminDTO(dto, new Club());
    if (dto.getAdmins().size() == 0)
      throw new IllegalArgumentException("The id of the admin cannot be null" );

    List<Student> admins = studentService.getStudents(dto.getAdmins());
    List<ClubMember> members = new ArrayList<>();
    admins.forEach(a -> {
      ClubMember member = new ClubMember();
      member.setFrom(getCurrentSchoolYear());
      member.setTo(member.getFrom());
      member.setStudent(a);
      member.setRole(ClubRole.SUPER_ADMIN);
      member.setClub(club);

      members.add(member);
    });
    club.setMembers(members);
    club.setFeed(new Feed());

    return ClubFactory.toView(
      clubRepository.save(club),
      true,
      feedService.isSubscribedToFeed(club)
    );
  }

  public ClubView updateClub(Long id, ClubDTO dto) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")" );

    // Update through reference so we don't need to get return value
    ClubFactory.fromDTO(dto, club);

    return ClubFactory.toView(
      clubRepository.save(club),
      true,
      feedService.isSubscribedToFeed(club)
    );
  }

  public ClubView updateClubAdmin(Long id, ClubAdminDTO dto) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")" );

    // Update through reference so we don't need to get return value
    ClubFactory.fromAdminDTO(dto, club);

    return ClubFactory.toView(
      clubRepository.save(club),
      true,
      feedService.isSubscribedToFeed(club)
    );
  }


  public String updateLogo(Long id, MultipartFile file) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")" );

    if (club.getLogoUrl() != null)
      fileHandler.delete(club.getLogoUrl());

    Map params = Map.of(
      "process", "resize",
      "sizes", StorageConfig.MEDIAS_CONF.get("club_avatar" ).sizes
    );
    club.setLogoUrl(fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("club_avatar" ).path, false, params));
    clubRepository.save(club);
    return club.getLogoUrl();
  }

  public String updateCover(Long id, MultipartFile file) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")" );

    if (club.getCoverUrl() != null || file == null)
      fileHandler.delete(club.getCoverUrl());

    if (file == null) {
      club.setLogoUrl(null);
    } else {
      Map params = Map.of(
        "process", "compress",
        "sizes", StorageConfig.MEDIAS_CONF.get("club_cover" ).sizes
      );
      club.setLogoUrl(fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("club_cover" ).path, false, params));
    }

    clubRepository.save(club);
    return club.getLogoUrl();
  }

  public ClubMember addMember(Long clubId, ClubMemberCreationDTO dto) {
    // Ensure that student is not already member of the club this year
    if (clubMemberRepository.existsByClubIdAndStudentIdAndFromYear(clubId, dto.getStudent(), getCurrentSchoolYear()))
      throw new IllegalArgumentException("this student is already part of this club" );

    ClubMember clubMember = new ClubMember();
    clubMember.setClub(getClub(clubId));
    clubMember.setStudent(studentService.getStudent(dto.getStudent()));

    clubMember.setRole(dto.getRole());
    clubMember.setPosition(dto.getPosition());
    clubMember.setFrom(dto.getYear());
    clubMember.setTo(dto.getYear());

    return clubMemberRepository.save(clubMember);
  }

  public ClubMember updateMember(Long id, ClubMemberDTO dto) {
    Optional<ClubMember> optionalClubMember = clubMemberRepository.findById(id);
    if (optionalClubMember.isEmpty())
      throw new IllegalArgumentException("could not find club member with id: " + id);

    ClubMember member = optionalClubMember.get();
    if (!SecurityService.hasRightOn(member.getClub()))
      throw new AuthException("You have not sufficient rights on this club (id:" + id + ")" );

    if (member.getRole() == ClubRole.ADMIN &&
      dto.getRole() != member.getRole() &&
      clubMemberRepository.findClubYearlyAdminCount(member.getClub(), ClubService.getCurrentSchoolYear()) == 1) {
      throw new IllegalArgumentException("Could not update member as club must have at least 1 admin" );
    }


    member.setPosition(dto.getPosition());
    member.setRole(dto.getRole());

    return clubMemberRepository.save(member);
  }


  public List<Club> getAll() {
    return clubRepository.findAllByOrderByName();
  }


  public List<Club> getUserCurrentClubsWith(Student student, ClubRole role) {
    return clubRepository.findCurrentByRoleWithInheritance(student, role, getCurrentSchoolYear());
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

  public List<ClubMemberProjection> getYearlyMembers(Long id, Integer year) {
    return clubMemberRepository.findClubYearlyMembers(id, year);
  }

  public Page<GalleryPreview> getClubGalleries(Long id, int page) {
    return galleryService.getClubGalleries(getClub(id), page);
  }

  public Set<StudentPreview> getAdmins(Long clubId) {
    return clubMemberRepository.findByClubIdAndRole(clubId, ClubRole.ADMIN)
      .stream()
      .map(m -> (StudentPreview) m.getStudent())
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
      throw new IllegalArgumentException("the student needs to be part of the club to be an admin" );

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
        throw new AuthException("no rights to modify this club" );
      }
    }
    clubMember.setRole(role);
    return clubMemberRepository.save(clubMember);
  }

  private ClubMember getMember(Long member) {
    Optional<ClubMember> clubMember = clubMemberRepository.findById(member);
    if (clubMember.isEmpty()) {
      throw new IllegalArgumentException("member could not be found" );
    }
    return clubMember.get();
  }

  public void removeMember(Long member, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    Club club = clubMember.getClub();
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      if (!payload.getClubsAdmin().contains(club.getId())) {
        throw new AuthException("no rights to modify this club" );
      }
    }
    clubMemberRepository.delete(clubMember);
    clubRepository.save(club);
  }

  public List<ClubMemberPreview> getStudentClubs(Long id) {
    return clubMemberRepository.findByStudentId(id)
      .stream()
      .map(ClubMemberFactory::toPreview)
      .collect(Collectors.toList());
  }

  public Set<Integer> getClubAllSchoolSessions(Long id) {
    return clubRepository.findClubSessions(id);
  }
}
