package com.iseplife.api.dao.club;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.dto.club.view.ClubAdminView;
import com.iseplife.api.dto.club.view.ClubMemberPreview;
import com.iseplife.api.dto.club.view.ClubMemberView;
import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.club.view.ClubView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.services.SecurityService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ClubFactory {
  @Lazy final private StudentFactory studentFactory;
  final private ModelMapper mapper;

  @PostConstruct()
  public void init() {
    mapper.typeMap(Club.class, ClubView.class)
      .addMappings(mapper ->
        mapper.map(src -> src.getFeed().getId(), ClubView::setFeed)
      );
    
    mapper.typeMap(ClubMember.class, ClubMemberView.class)
      .addMappings(mapper -> {
        mapper
          .when(src -> src.getParent() != null)
          .map(src -> src.getParent().getId(), ClubMemberView::setParent);
        mapper
          .using(ctx -> studentFactory.toPreview((Student) ctx.getSource()))
          .map(ClubMember::getStudent, ClubMemberView::setStudent);
      });
    
    mapper.typeMap(ClubMember.class, ClubMemberPreview.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> toPreview((Club) ctx.getSource()))
          .map(ClubMember::getClub, ClubMemberPreview::setClub);
      });
  }

  public ClubView toView(Club club, SubscriptionProjection subProjection){
    ClubView view = mapper.map(club, ClubView.class);
    view.setSubscribed(subProjection);
    view.setCanEdit(SecurityService.hasRightOn(club));

    return view;
  }

  public ClubAdminView toAdminView(Club club){
    return mapper.map(club, ClubAdminView.class);
  }

  public ClubPreview toPreview(Club club){
    return mapper.map(club, ClubPreview.class);
  }

  public ClubMemberView toView(ClubMember member) {
    return mapper.map(member, ClubMemberView.class);
  }

  public ClubMemberPreview toPreview(ClubMember member) {
    return mapper.map(member, ClubMemberPreview.class);
  }

}
