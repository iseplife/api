package com.iseplive.api.entity.user;

import com.iseplive.api.constants.AuthorTypes;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(AuthorTypes.EMPLOYEE)
public class Employee extends Author {

  private String firstname;
  private String lastname;

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
}
