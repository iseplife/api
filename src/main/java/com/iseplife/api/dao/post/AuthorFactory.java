package com.iseplife.api.dao.post;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import org.springframework.stereotype.Component;

public class AuthorFactory {

  public static AuthorView toView(Club club) {
    AuthorView author = new AuthorView();
    author.setId(club.getId());
    author.setAuthorType(AuthorType.CLUB);
    author.setName(club.getName());
    author.setThumbnail(club.getLogoUrl());

    return author;
  }

  public static AuthorView toView(Student student) {
    AuthorView author = new AuthorView();
    author.setId(student.getId());
    author.setAuthorType(AuthorType.STUDENT);
    author.setName(student.getFirstName() + " " + student.getLastName());
    author.setThumbnail(student.getPicture());

    return author;
  }

  public static AuthorView adminToView() {
    AuthorView author = new AuthorView();
    author.setId(1L);
    author.setAuthorType(AuthorType.ADMIN);
    author.setName("Admin");
    author.setThumbnail(null);

    return author;
  }
}
