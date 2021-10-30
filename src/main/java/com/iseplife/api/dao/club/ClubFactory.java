package com.iseplife.api.dao.club;

import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.club.view.*;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ClubFactory {
  @Lazy final private StudentFactory studentFactory;
  final private ModelMapper mapper;


  public ClubView toView(Club club, Boolean isSubscribed){
    mapper
      .typeMap(Club.class, ClubView.class)
      .addMappings(mapper ->
        mapper.map(src -> src.getFeed().getId(), ClubView::setFeed)
      );

    ClubView view = mapper.map(club, ClubView.class);
    view.setSubscribed(isSubscribed);
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
    mapper
      .typeMap(ClubMember.class, ClubMemberView.class)
      .addMappings(mapper -> {
        mapper
          .when(src -> src.getParent() != null)
          .map(src -> src.getParent().getId(), ClubMemberView::setParent);
        mapper
          .using(ctx -> studentFactory.toPreview((Student) ctx.getSource()))
          .map(ClubMember::getStudent, ClubMemberView::setStudent);
      });

    return mapper.map(member, ClubMemberView.class);
  }

  public ClubMemberPreview toPreview(ClubMember member) {
    mapper
      .typeMap(ClubMember.class, ClubMemberPreview.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> toPreview((Club) ctx.getSource()))
          .map(ClubMember::getClub, ClubMemberPreview::setClub);
      });
    return mapper.map(member, ClubMemberPreview.class);
  }

}
