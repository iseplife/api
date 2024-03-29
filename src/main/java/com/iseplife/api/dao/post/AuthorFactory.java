package com.iseplife.api.dao.post;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;

public class AuthorFactory {

  public static AuthorView toView(Club club) {
    AuthorView author = new AuthorView();
    author.setId(club.getId());
    author.setAuthorType(AuthorType.CLUB);
    author.setName(club.getName());
    author.setThumbnail(club.getLogoUrl());
    author.setFeedId(club.getFeed().getId());

    return author;
  }

  public static AuthorView toView(Student student) {
    AuthorView author = new AuthorView();
    author.setId(student.getId());
    author.setAuthorType(AuthorType.STUDENT);
    author.setName(student.getFirstName() + " " + student.getLastName());
    author.setThumbnail(student.getPicture());
    author.setFeedId(student.getFeed().getId());

    return author;
  }
}
