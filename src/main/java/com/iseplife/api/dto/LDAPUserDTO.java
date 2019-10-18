package com.iseplife.api.dto;

public class LDAPUserDTO {

  private String firstName;
  private String lastName;
  private String fullName;
  private String employeeType;
  private String employeeNumber;
  private String login;
  private String password;
  private String mail;

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

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(String employeeType) {
    this.employeeType = employeeType;
  }

  public String getEmployeeNumber() {
    return employeeNumber;
  }

  public void setEmployeeNumber(String employeeNumber) {
    this.employeeNumber = employeeNumber;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  @Override
  public String toString() {
    return "LDAPUserDTO{" +
      "firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", fullName='" + fullName + '\'' +
      ", employeeType='" + employeeType + '\'' +
      ", employeeNumber='" + employeeNumber + '\'' +
      ", login='" + login + '\'' +
      ", password='" + password + '\'' +
      ", mail='" + mail + '\'' +
      '}';
  }
}
