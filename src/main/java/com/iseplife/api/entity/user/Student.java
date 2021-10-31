package com.iseplife.api.entity.user;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.constants.Language;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.subscription.Subscription;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Student implements UserDetails, Author {
  @Id
  @Column(unique = true)
  private Long id;

  private Integer promo;
  private Date archivedAt;
  private Date lastConnection;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;


  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private Boolean recognition = false;
  private Boolean notification = false;
  private Language language = Language.FR;

  private Integer mediaCounter;
  private Date mediaCooldown;

  @NotNull
  private Boolean hasDefaultPicture = false;

  private String picture;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;

  @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL)
  private List<Subscription> subscriptions;

  @Override
  public String getName() {
    return firstName + " " + lastName;
  }

  @Override
  public AuthorType getAuthorType() {
    return AuthorType.STUDENT;
  }

  @Override
  public String getThumbnail() {
    return picture;
  }


  public boolean isArchived() { return !(archivedAt == null || archivedAt.getTime() > new Date().getTime()); }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
