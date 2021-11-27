package com.iseplife.api.dao.group;

import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.dto.group.GroupCreationDTO;
import com.iseplife.api.dto.group.GroupUpdateDTO;
import com.iseplife.api.dto.group.view.GroupAdminView;
import com.iseplife.api.dto.group.view.GroupMemberView;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.entity.group.GroupMember;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupFactory {
  @Lazy final private StudentFactory studentFactory;
  final private ModelMapper mapper;

  public GroupView toView(Group group, SubscriptionProjection subProjection) {
    mapper
      .typeMap(Group.class, GroupView.class)
      .addMappings(mapper -> {
        mapper.map(src -> src.getFeed().getId(), GroupView::setFeed);
      });

    GroupView view = mapper.map(group, GroupView.class);
    view.setSubscribed(subProjection);

    return view;
  }

  public GroupAdminView toAdminView(Group group, List<GroupMember> admins) {
    GroupAdminView view = mapper.map(group, GroupAdminView.class);
    view.setLocked(group.getType() != GroupType.DEFAULT);
    view.setAdmins(admins.stream().map(this::toView).collect(Collectors.toList()));

    return view;
  }

  public GroupPreview toPreview(Group group){
    return mapper.map(group, GroupPreview.class);
  }

  public GroupMemberView toView(GroupMember member) {
    mapper
      .typeMap(GroupMember.class, GroupMemberView.class)
      .addMappings(mapper ->  {
        mapper
          .using(ctx -> studentFactory.toPreview((Student) ctx.getSource()))
          .map(GroupMember::getStudent, GroupMemberView::setStudent);
      });

    return mapper.map(member, GroupMemberView.class);
  }
}
