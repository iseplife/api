package com.iseplife.api.dao.group;

import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.group.view.GroupMemberView;
import com.iseplife.api.entity.group.GroupMember;

public class GroupMemberFactory {

  static public GroupMemberView toView(GroupMember member) {
    GroupMemberView view = new GroupMemberView();
    view.setId(member.getId());
    view.setAdmin(member.isAdmin());
    view.setStudent(StudentFactory.toPreview(member.getStudent()));

    return view;
  }
}
