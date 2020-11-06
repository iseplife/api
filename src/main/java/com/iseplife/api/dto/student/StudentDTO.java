package com.iseplife.api.dto.student;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 29/07/2017.
 * back
 */
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getPromo() {
    return promo;
  }

  public void setPromo(Integer promo) {
    this.promo = promo;
  }


  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getMailISEP() {
    return mailISEP;
  }

  public void setMailISEP(String mailISEP) {
    this.mailISEP = mailISEP;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

}
