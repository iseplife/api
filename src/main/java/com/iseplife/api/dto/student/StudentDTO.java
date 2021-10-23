package com.iseplife.api.dto.student;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class StudentDTO {
  private Long id;
  private Integer promo;
  private String firstName;
  private String lastName;
  private Date birthDate;
  private String phone;
  private String mail;
  private String mailISEP;
  private String address;
  private List<String> roles;
}
