package com.iseplive.api.entity.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.ClubRoleEnum;
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

  // Determine the interactions allowed by the student
  private ClubRoleEnum role;

  // Student's position in club, doesn't have any effect
  private String position;

  @ManyToOne
  private Student student;

  @ManyToOne
  private ClubMember parent;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() { return student; }

  public void setStudent(Student student) { this.student = student; }

  public ClubRoleEnum getRole() { return role; }

  public void setRole(ClubRoleEnum role) { this.role = role; }

  public Club getClub() { return club; }

  public void setClub(Club club) { this.club = club; }

  public ClubMember getParent() { return parent; }

  public void setParent(ClubMember parent) { this.parent = parent; }

  public String getPosition() { return position; }

  public void setPosition(String position) { this.position = position; }
}
