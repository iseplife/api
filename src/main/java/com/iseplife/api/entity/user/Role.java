package com.iseplife.api.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Getter @Setter @NoArgsConstructor
public class Role implements GrantedAuthority {

  public Role(String role){ this.role = role; }

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String role;

  @Override
  public String getAuthority() {
    return role;
  }
}
