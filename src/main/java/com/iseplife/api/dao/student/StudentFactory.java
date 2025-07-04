package com.iseplife.api.dao.student;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.constants.Language;
import com.iseplife.api.dao.subscription.projection.NotificationCountProjection;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.dto.student.StudentSettingsDTO;
import com.iseplife.api.dto.student.view.*;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.utils.MediaUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class StudentFactory {
  final private ModelMapper mapper;

  @SuppressWarnings("unchecked")
  @PostConstruct()
  public void init() {
    mapper.typeMap(Student.class, StudentView.class)
      .addMappings(mapper -> {
        mapper
        .using(ctx -> toPictures(((Student)ctx.getSource()).getPicture(), ((Student)ctx.getSource()).getHasDefaultPicture()))
        .map(
          source -> source,
          StudentView::setPictures
        );
        mapper
          .using(ctx -> ((Set<Role>) ctx.getSource()).stream().map(Role::getRole).collect(Collectors.toList()))
          .map(Student::getRoles, StudentView::setRoles);
        mapper
          .map(src -> src.getFeed().getId(), StudentView::setFeedId);
      });

    mapper.typeMap(Student.class, StudentPreviewAdmin.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ((Set<Role>) ctx.getSource()).stream().map(Role::getRole).collect(Collectors.toList()))
          .map(Student::getRoles, StudentPreviewAdmin::setRoles);
      });

    mapper.typeMap(Student.class, StudentAdminView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ((Set<Role>) ctx.getSource()).stream().map(Role::getRole).collect(Collectors.toList()))
          .map(Student::getRoles, StudentAdminView::setRoles);
      });

    // Also cover LoggedStudentPreview class
    mapper.typeMap(Student.class, StudentPreview.class)
      .addMappings(mapper ->
        mapper
          .map(src -> src.getFeed().getId(), StudentPreview::setFeedId)
      );

    mapper.typeMap(StudentSettingsDTO.class, Student.class).addMappings(mapper ->
      mapper.using(ctx -> Language.valueOf(((String) ctx.getSource()).toUpperCase())).map(StudentSettingsDTO::getLanguage, Student::setLanguage)
    );
  }

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

  public LoggedStudentPreview toSelfPreview(Student student, NotificationCountProjection count){
    LoggedStudentPreview selfPreview = mapper.map(student, LoggedStudentPreview.class);
    selfPreview.setUnwatchedNotifications(count.getUnwatched());
    selfPreview.setTotalNotifications(count.getCount());
    selfPreview.setPasswordSetup(student.getPassword() != null && student.getPassword().length() > 0);

    return selfPreview;
  }

  public StudentPreview toPreview(Student student) {
    return mapper.map(student, StudentPreview.class);
  }


  public StudentView toView(Student student) {
    return mapper.map(student, StudentView.class);
  }

  public StudentPreviewAdmin toPreviewAdmin(Student student) {
    return mapper.map(student, StudentPreviewAdmin.class);
  }

  public StudentAdminView toAdminView(Student student) {
    return mapper.map(student, StudentAdminView.class);
  }

  public StudentOverview toOverview(Student student, SubscriptionProjection subscription) {
    StudentOverview overview = mapper.map(student, StudentOverview.class);
    overview.setSubscribed(subscription);
    return overview;
  }

}
