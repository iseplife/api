package com.iseplive.api.dto.student;

import java.util.Date;

/**
 * Created by Guillaume on 29/07/2017.
 * back
 */
public class StudentDTO {
  private String promo;
  private String firstname;
  private String lastname;
  private Date birthDate;
  private String phone;
  private String bio;
  private String mail;
  private String mailISEP;
  private String address;

  public String getPromo() {
    return promo;
  }

  public void setPromo(String promo) {
    this.promo = promo;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
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

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
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

}
