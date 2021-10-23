package com.iseplife.api.dto.student.view;

import lombok.Data;

@Data
public class StudentOverview {
  private Long id;
  private String picture;
  private Integer promo;
  private boolean archived;
  private String firstName;
  private String lastName;
  private String mail;
  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;
}
