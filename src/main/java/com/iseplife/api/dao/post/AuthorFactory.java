package com.iseplife.api.dao.post;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import org.springframework.stereotype.Component;

@Component
public class AuthorFactory {

  public AuthorView entitytoView(Club club) {
    AuthorView author = new AuthorView();
    author.setId(club.getId());
    author.setType(AuthorType.CLUB);
    author.setName(club.getName());
    author.setThumbnail(club.getLogoUrl());

    return author;
  }

  public AuthorView entitytoView(Student student) {
    AuthorView author = new AuthorView();
    author.setId(student.getId());
    author.setType(AuthorType.STUDENT);
    author.setName(student.getFirstName() + " " + student.getLastName());
    author.setThumbnail(student.getPicture());

    return author;
  }

  public AuthorView admintoView() {
    AuthorView author = new AuthorView();
    author.setId(1L);
    author.setType(AuthorType.ADMIN);
    author.setName("Admin");
    author.setThumbnail(null);

    return author;
  }
}
