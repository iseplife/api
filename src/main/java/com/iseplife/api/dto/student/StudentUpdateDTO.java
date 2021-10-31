package com.iseplife.api.dto.student;

import lombok.Data;

import java.util.Date;

@Data
public class StudentUpdateDTO {
  private Long id;
  private Date birthDate;
  private String mail;
  private String address;
  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;
}
