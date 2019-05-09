package com.iseplive.api.entity.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.entity.user.Student;

import javax.persistence.*;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Entity
public class ClubMember {
  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnore
  @ManyToOne
  private Club club;

  @ManyToOne
  private ClubRole role;

  @ManyToOne
  private Student member;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getMember() {
    return member;
  }

  public void setMember(Student member) {
    this.member = member;
  }

  public ClubRole getRole() {
    return role;
  }

  public void setRole(ClubRole role) {
    this.role = role;
  }

  public Club getClub() {
    return club;
  }

  public void setClub(Club club) {
    this.club = club;
  }
}
