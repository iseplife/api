package com.iseplife.api.dao.student;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.dto.student.view.*;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.utils.MediaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class StudentFactory {

  public static StudentPictures toPictures(String picture, Boolean hasDefaultPicture) {
    if (MediaUtils.isOriginalPicture(picture)) {
      return new StudentPictures(
        picture,
        null
      );
    } else {
      return new StudentPictures(
        hasDefaultPicture ?
          StorageConfig.MEDIAS_CONF.get("user_original").path + "/" + MediaUtils.extractFilename(picture) :
          null,
        picture
      );
    }
  }

  public static StudentPreview toPreview(Student student) {
    StudentPreview studentPreview = new StudentPreview();

    studentPreview.setId(student.getId());
    studentPreview.setFirstName(student.getFirstName());
    studentPreview.setLastName(student.getLastName());
    studentPreview.setPromo(student.getPromo());
    studentPreview.setPicture(student.getPicture());

    return studentPreview;
  }


  public static StudentView toView(Student student) {
    StudentView view = new StudentView();

    view.setId(student.getId());
    view.setFirstName(student.getFirstName());
    view.setLastName(student.getLastName());
    view.setPromo(student.getPromo());


    view.setPictures(toPictures(student.getPicture(), student.getHasDefaultPicture()));


    view.setBirthDate(student.getBirthDate());
    view.setFacebook(student.getFacebook());
    view.setTwitter(student.getTwitter());
    view.setInstagram(student.getInstagram());

    view.setNotification(student.getNotification());
    view.setRecognition(student.getRecognition());

    view.setRoles(
      student.getRoles()
        .stream()
        .map(Role::getRole)
        .collect(Collectors.toList())
    );

    return view;
  }

  public static StudentPreviewAdmin toPreviewAdmin(Student student) {
    StudentPreviewAdmin studentPreviewAdmin = new StudentPreviewAdmin();

    studentPreviewAdmin.setId(student.getId());
    studentPreviewAdmin.setFirstName(student.getFirstName());
    studentPreviewAdmin.setLastName(student.getLastName());
    studentPreviewAdmin.setPromo(student.getPromo());
    studentPreviewAdmin.setPicture(student.getPicture());
    studentPreviewAdmin.setArchived(student.isArchived());
    studentPreviewAdmin.setRoles(
      student.getRoles()
        .stream()
        .map(Role::getRole)
        .collect(Collectors.toList())
    );

    return studentPreviewAdmin;
  }

  public static StudentAdminView toAdminView(Student student) {
    StudentAdminView studentAdmin = new StudentAdminView();

    studentAdmin.setId(student.getId());

    studentAdmin.setFirstName(student.getFirstName());
    studentAdmin.setLastName(student.getLastName());
    studentAdmin.setPromo(student.getPromo());
    studentAdmin.setPictures(toPictures(student.getPicture(), student.getHasDefaultPicture()));

    studentAdmin.setMail(student.getMail());

    studentAdmin.setBirthDate(student.getBirthDate());
    studentAdmin.setFacebook(student.getFacebook());
    studentAdmin.setTwitter(student.getTwitter());
    studentAdmin.setInstagram(student.getInstagram());

    studentAdmin.setArchived(student.isArchived());
    studentAdmin.setRoles(
      student.getRoles()
        .stream()
        .map(Role::getRole)
        .collect(Collectors.toList())
    );

    return studentAdmin;
  }

  public static StudentOverview toOverview(Student student) {
    StudentOverview overview = new StudentOverview();

    overview.setId(student.getId());
    overview.setPromo(student.getPromo());
    overview.setPicture(student.getPicture());
    overview.setArchived(student.isArchived());

    overview.setFirstName(student.getFirstName());
    overview.setLastName(student.getLastName());

    overview.setMail(student.getMail());

    overview.setFacebook(student.getFacebook());
    overview.setTwitter(student.getTwitter());
    overview.setSnapchat(student.getSnapchat());
    overview.setInstagram(student.getInstagram());

    return overview;
  }

}
