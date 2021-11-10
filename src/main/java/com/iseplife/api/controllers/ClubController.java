package com.iseplife.api.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.post.CommentFactory;
import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dao.student.projection.StudentPreviewProjection;
import com.iseplife.api.dto.club.ClubAdminDTO;
import com.iseplife.api.dto.club.ClubDTO;
import com.iseplife.api.dto.club.ClubMemberCreationDTO;
import com.iseplife.api.dto.club.ClubMemberDTO;
import com.iseplife.api.dto.club.view.ClubAdminView;
import com.iseplife.api.dto.club.view.ClubMemberPreview;
import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.club.view.ClubView;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.media.view.MediaNameView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.services.ClubService;
import com.iseplife.api.services.FeedService;
import com.iseplife.api.services.PostService;
import com.iseplife.api.services.ThreadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {
  final private PostService postService;
  final private SubscriptionService subscriptionService;
  final private ThreadService threadService;
  final private ClubService clubService;
  final private PostFactory postFactory;
  final private GalleryFactory galleryFactory;
  final private ClubFactory factory;
  final private CommentFactory commentFactory;

  @GetMapping
  @RolesAllowed({Roles.STUDENT})
  public List<ClubPreview> getAllClubs() {
    return clubService.getAll().stream()
      .map(factory::toPreview)
      .collect(Collectors.toList());
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN})
  public ClubView createClub(@RequestBody ClubAdminDTO dto) {
    return factory.toView(clubService.createClub(dto), null);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public ClubView getClub(@PathVariable Long id) {
    Club club = clubService.getClub(id);
    return factory.toView(club, subscriptionService.getSubscriptionProjection(club));
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public ClubView updateClub(@PathVariable Long id, @RequestBody ClubDTO dto) {
    Club club = clubService.updateClub(id, dto);
    return factory.toView(club, subscriptionService.getSubscriptionProjection(club));
  }

  @PutMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN})
  public ClubAdminView updateAdminClub(@PathVariable Long id, @RequestBody ClubAdminDTO dto) {
    return factory.toAdminView(clubService.updateClubAdmin(id, dto));
  }

  @PutMapping("/{id}/logo")
  @RolesAllowed({Roles.STUDENT})
  public MediaNameView updateLogo(@PathVariable Long id, @RequestParam(value="file", required = false) MultipartFile file) {
    return MediaFactory.toNameView(clubService.updateLogo(id, file));
  }

  @PutMapping("/{id}/cover")
  @RolesAllowed({Roles.STUDENT})
  public MediaNameView updateCover(@PathVariable Long id, @RequestParam(value = "file", required = false) MultipartFile file) {
    return MediaFactory.toNameView(clubService.updateCover(id, file));
  }

  @PutMapping("/{id}/archive")
  @RolesAllowed({Roles.ADMIN})
  public Boolean toggleArchiveStatus(@PathVariable Long id) {
    return clubService.toggleArchiveStatus(id);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public void deleteClub(@PathVariable Long id) {
    clubService.deleteClub(id);
  }

  @GetMapping("/{id}/galleries")
  @RolesAllowed({Roles.STUDENT})
  public Page<GalleryPreview> getGalleries(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return clubService.getClubGalleries(id, page).map(galleryFactory::toPreview);
  }

  @GetMapping("/{id}/admins")
  @RolesAllowed({Roles.STUDENT})
  public Set<StudentPreviewProjection> getAdmins(@PathVariable Long id) {
    return clubService.getAdmins(id);
  }

  @PostMapping("/{id}/member")
  @RolesAllowed({Roles.STUDENT})
  public ClubMemberProjection addMember(@PathVariable Long id, @RequestBody ClubMemberCreationDTO dto) {
    return factory.toView(clubService.addMember(id, dto));
  }

  @GetMapping("/student/{id}")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubMemberPreview> getStudentClubs(@PathVariable Long id) {
    return clubService.getStudentClubs(id).stream()
      .map(factory::toPreview)
      .collect(Collectors.toList());
  }

  @PutMapping("/member/{member}")
  @RolesAllowed({Roles.STUDENT})
  public ClubMemberProjection updateMember(@PathVariable Long member, @RequestBody ClubMemberDTO dto) {
    return factory.toView(clubService.updateMember(member, dto));
  }

  @GetMapping("/{id}/member")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubMemberProjection> getYearlyMembers(@PathVariable Long id, @RequestParam(name = "y", required = false) Integer year) {
    return clubService.getYearlyMembers(id, year);
  }

  @GetMapping("/{id}/school-sessions")
  @RolesAllowed({Roles.STUDENT})
  public Set<Integer> getClubSchoolSessions(@PathVariable Long id) {
    return clubService.getClubAllSchoolSessions(id);
  }


  @DeleteMapping("/member/{member}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteMember(@PathVariable Long member,
                           @AuthenticationPrincipal TokenPayload payload) {
    clubService.removeMember(member, payload);
  }

  @GetMapping("/{id}/post")
  @RolesAllowed({Roles.STUDENT})
  public Page<PostProjection> getPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal TokenPayload token) {
    return postService.getAuthorPosts(id, page, token).map(p -> {
      CommentProjection trendingComment = threadService.getTrendingComment(p.getThread());
      return postFactory.toView(
          p,
          threadService.isLiked(p.getThread()),
          trendingComment == null ? null : commentFactory.toView(trendingComment, threadService.isLiked(trendingComment.getThread()))
      );
    });
  }
}
