package com.iseplife.api.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.iseplife.api.entity.user.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.constants.FeedType;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.club.ClubMemberRepository;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dao.club.projection.ClubMemberStudentProjection;
import com.iseplife.api.dao.gallery.EventGalleryProjection;
import com.iseplife.api.dao.student.projection.StudentPreviewProjection;
import com.iseplife.api.dto.club.ClubAdminDTO;
import com.iseplife.api.dto.club.ClubDTO;
import com.iseplife.api.dto.club.ClubMemberCreationDTO;
import com.iseplife.api.dto.club.ClubMemberDTO;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubService {
  @Lazy final private StudentService studentService;
  @Lazy final private GalleryService galleryService;
  final private ModelMapper mapper;
  final private ClubRepository clubRepository;
  final private ClubMemberRepository clubMemberRepository;

  @Qualifier("FileHandlerBean") final private FileHandler fileHandler;

  final public static int MAX_DESCRIPTION_LENGTH = 2000;

  public Club getClub(Long id) {
    Optional<Club> club = clubRepository.findById(id);
    if (club.isEmpty())
      throw new HttpNotFoundException("club_not_found");

    return club.get();
  }

  public static Integer getCurrentSchoolYear() {
    Calendar c = Calendar.getInstance();
    return c.get(Calendar.MONTH) >= Calendar.SEPTEMBER ?
      c.get(Calendar.YEAR) :
      c.get(Calendar.YEAR) - 1;
  }


  public Club createClub(ClubAdminDTO dto) {
    Club club = mapper.map(dto, Club.class);
    if (dto.getAdmins().size() == 0)
      throw new HttpBadRequestException("admins_required");

    if(dto.getDescription().length() > MAX_DESCRIPTION_LENGTH)
      throw new HttpBadRequestException("club_description_too_long");

    List<Student> admins = studentService.getStudents(dto.getAdmins());
    List<ClubMember> members = new ArrayList<>();
    admins.forEach(a -> {
      ClubMember member = new ClubMember();
      member.setFromYear(getCurrentSchoolYear());
      member.setToYear(member.getFromYear());
      member.setStudent(a);
      member.setRole(ClubRole.SUPER_ADMIN);
      member.setClub(club);

      members.add(member);
    });
    club.setMembers(members);
    club.setFeed(new Feed(dto.getName(), FeedType.CLUB));

    return clubRepository.save(club);
  }

  public Club updateClub(Long id, ClubDTO dto) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    if(dto.getDescription().length() > MAX_DESCRIPTION_LENGTH)
      throw new HttpBadRequestException("club_description_too_long");

    mapper.map(dto, club);
    return clubRepository.save(club);
  }

  public Club updateClubAdmin(Long id, ClubAdminDTO dto) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    // Update through reference so we don't need to get return value
    mapper.map(dto, club);

    return clubRepository.save(club);
  }


  public String updateLogo(Long id, MultipartFile file) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    if (club.getLogoUrl() != null)
      fileHandler.delete(club.getLogoUrl());

    Map params = Map.of(
      "process", "resize",
      "sizes", StorageConfig.MEDIAS_CONF.get("club_avatar").sizes
    );
    try {
      club.setLogoUrl(fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("club_avatar").path, false, params));
    } catch (IOException e) {
      e.printStackTrace();
      throw new HttpBadRequestException("media_upload_failed", e);
    }
    clubRepository.save(club);
    return club.getLogoUrl();
  }

  public String updateCover(Long id, MultipartFile file) {
    Club club = getClub(id);
    if (!SecurityService.hasRightOn(club))
      throw new HttpForbiddenException("insufficient_rights");

    // Delete previous cover
    if (club.getCoverUrl() != null)
      fileHandler.delete(club.getCoverUrl());

    if (file == null) {
      club.setCoverUrl(null);
    } else {
      Map params = Map.of(
        "process", "compress",
        "sizes", StorageConfig.MEDIAS_CONF.get("club_cover").sizes
      );
      try {
        club.setCoverUrl(fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("club_cover").path, false, params));
      } catch (IOException e) {
        e.printStackTrace();
        throw new HttpBadRequestException("media_upload_failed", e);
      }
    }

    clubRepository.save(club);
    return club.getCoverUrl();
  }

  public ClubMember addMember(Long clubId, ClubMemberCreationDTO dto) {
    // Ensure that student is not already member of the club this year
    if (clubMemberRepository.existsByClubIdAndStudentIdAndFromYear(clubId, dto.getStudent(), getCurrentSchoolYear()))
      throw new HttpBadRequestException("member_already_exist");

    ClubMember clubMember = new ClubMember();
    clubMember.setClub(getClub(clubId));
    clubMember.setStudent(studentService.getStudent(dto.getStudent()));

    clubMember.setRole(dto.getRole());
    clubMember.setPosition(dto.getPosition());
    clubMember.setFromYear(dto.getYear());
    clubMember.setToYear(dto.getYear());

    return clubMemberRepository.save(clubMember);
  }

  public ClubMember updateMember(Long id, ClubMemberDTO dto) {
    Optional<ClubMember> optionalClubMember = clubMemberRepository.findById(id);
    if (optionalClubMember.isEmpty())
      throw new HttpNotFoundException("member_not_found");

    ClubMember member = optionalClubMember.get();
    if (!SecurityService.hasRightOn(member.getClub()))
      throw new HttpForbiddenException("insufficient_rights");

    if (member.getRole() == ClubRole.ADMIN &&
      dto.getRole() != member.getRole() &&
      clubMemberRepository.findClubYearlyAdminCount(member.getClub(), ClubService.getCurrentSchoolYear()) == 1) {
      throw new HttpBadRequestException("minimum_admins_size_required");
    }


    member.setPosition(dto.getPosition());
    member.setRole(dto.getRole());

    return clubMemberRepository.save(member);
  }


  public List<Club> getAll() {
    return clubRepository.findAllByOrderByName();
  }


  public List<Club> getUserCurrentClubsWith(Student student, ClubRole role) {
    return clubRepository.findCurrentByRoleWithInheritance(student, role, getCurrentSchoolYear(), student.getRoles().stream().anyMatch(r -> r.getRole().equals(Roles.ADMIN)));
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
    if (year == null)
      year = ClubService.getCurrentSchoolYear();

    return clubMemberRepository.findClubYearlyMembers(id, year);
  }

  public Page<Gallery> getClubGalleries(Long id, int page) {
    return null;//DANGEROUS (can get galleries from events we don't have access ! galleryService.getClubGalleries(getClub(id), page);
  }
  public Page<EventGalleryProjection> getClubEventsGalleries(TokenPayload payload, Long id, int page) {
    return galleryService.getEventsGalleriesFrom(payload, id, page);
  }

  public Set<StudentPreviewProjection> getAdmins(Long clubId) {
    return clubMemberRepository.findByClubIdAndRole(clubId, ClubRole.ADMIN)
      .stream()
      .map(ClubMemberProjection::getStudent)
      .collect(Collectors.toSet());
  }

  public Set<Student> getAdmins(Club club) {
    return club.getMembers()
      .stream()
      .filter(s -> s.getRole().is(ClubRole.ADMIN))
      .map(ClubMember::getStudent)
      .collect(Collectors.toSet());
  }

  public ClubMember updateMemberRole(Long member, ClubRole role, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      if (!payload.getClubsAdmin().contains(clubMember.getClub().getId())) {
        throw new HttpForbiddenException("insufficient_rights");
      }
    }
    clubMember.setRole(role);
    return clubMemberRepository.save(clubMember);
  }

  private ClubMember getMember(Long member) {
    Optional<ClubMember> clubMember = clubMemberRepository.findById(member);
    if (clubMember.isEmpty()) {
      throw new HttpNotFoundException("member_not_found");
    }
    return clubMember.get();
  }

  public void removeMember(Long member, TokenPayload payload) {
    ClubMember clubMember = getMember(member);
    Club club = clubMember.getClub();
    if (!payload.getRoles().contains(Roles.ADMIN)) {
      if (!payload.getClubsAdmin().contains(club.getId())) {
        throw new HttpForbiddenException("insufficient_rights");
      }
    }
    clubMemberRepository.delete(clubMember);
    clubRepository.save(club);
  }

  public List<ClubMemberStudentProjection> getStudentClubs(Long id) {
    return clubMemberRepository.findByStudentId(id);
  }

  public Set<Integer> getClubAllSchoolSessions(Long id) {
    return clubRepository.findClubSessions(id);
  }
}
