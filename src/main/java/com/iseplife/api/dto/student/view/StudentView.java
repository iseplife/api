package com.iseplife.api.dto.student.view;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentView {
  private Long id;
  private Long feedId;

  private Integer promo;
  private StudentPictures pictures;
  private Date archivedAt;
  private Date lastConnection;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;

  private Boolean recognition;
  private Boolean notification;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private List<String> roles;
}
