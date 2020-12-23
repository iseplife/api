package com.iseplife.api.dao.group;

import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dto.group.GroupDTO;
import com.iseplife.api.dto.group.view.GroupAdminView;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.entity.GroupMember;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.services.SecurityService;

import java.util.stream.Collectors;

public class GroupFactory {

  static public void updateFromDTO(Group group, GroupDTO dto){
    group.setName(dto.getName());
    group.setRestricted(dto.getRestricted());
  }

  static public Group fromDTO(GroupDTO dto){
    Group group = new Group();
    group.setName(dto.getName());
    group.setRestricted(dto.getRestricted());
    group.setFeed(new Feed());

    return group;
  }

  static public GroupView toView(Group group, Boolean isSubscribed) {
    GroupView view = new GroupView();

    view.setId(group.getId());
    view.setName(group.getName());
    view.setRestricted(group.isRestricted());
    view.setArchived(group.isArchived());
    view.setCover(group.getCover());
    view.setFeed(group.getFeed().getId());
    view.setHasRight(SecurityService.hasRightOn(group));
    view.setSubscribed(isSubscribed);

    return view;
  }

  static public GroupAdminView toAdminView(Group group) {
    GroupAdminView view = new GroupAdminView();

    view.setId(group.getId());
    view.setName(group.getName());
    view.setRestricted(group.isRestricted());
    view.setArchived(group.isArchived());
    view.setCover(group.getCover());
    view.setLocked(group.getType() != GroupType.DEFAULT);
    view.setAdmins(
      group.getMembers()
        .stream()
        .filter(GroupMember::isAdmin)
        .map(GroupMemberFactory::toView)
        .collect(Collectors.toList())
    );

    return view;
  }

  static public GroupPreview toPreview(Group group){
    GroupPreview preview = new GroupPreview();
    preview.setId(group.getId());
    preview.setName(group.getName());
    preview.setCover(group.getCover());

    return preview;
  }
}
