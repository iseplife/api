package com.iseplife.api.dto.student.view;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentAdminView {
  private Long id;
  private boolean archived;
  private Integer promo;
  private StudentPictures pictures;
  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;
  private String phoneNumber;
  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;
  private List<String> roles;
}
