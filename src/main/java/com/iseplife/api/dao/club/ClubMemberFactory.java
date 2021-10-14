package com.iseplife.api.dao.club;

import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.club.view.ClubMemberPreview;
import com.iseplife.api.dto.club.view.ClubMemberView;
import com.iseplife.api.entity.club.ClubMember;

public class ClubMemberFactory {
    static public ClubMemberView toView(ClubMember member) {
      ClubMemberView view = new ClubMemberView();
      view.setId(member.getId());
      view.setRole(member.getRole());
      view.setPosition(member.getPosition());
      view.setStudent(StudentFactory.toPreview(member.getStudent()));

      if(member.getParent() != null)
        view.setParent(member.getParent().getId());

      return view;
    }

  static public ClubMemberPreview toPreview(ClubMember member) {
    ClubMemberPreview preview = new ClubMemberPreview();
    preview.setId(member.getId());
    preview.setPosition(member.getPosition());
    preview.setClub(ClubFactory.toPreview(member.getClub()));

    return preview;
  }
}
