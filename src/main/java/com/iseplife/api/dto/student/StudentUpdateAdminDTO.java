package com.iseplife.api.dto.student;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class StudentUpdateAdminDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private Integer promo;
  private Date birthDate;
  private String mail;
  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;
  private List<String> roles;
}
