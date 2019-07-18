package com.iseplive.api.entity.user;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
@Entity
public class Role implements GrantedAuthority {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String role;

  public Role() {}
  public Role(String role){ this.role = role; }

  public Long getId() { return id; }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String getAuthority() {
    return role;
  }
}
