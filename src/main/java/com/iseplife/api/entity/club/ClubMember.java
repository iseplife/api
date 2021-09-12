package com.iseplife.api.entity.club;

import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.ClubRole;

import javax.persistence.*;
import java.util.Calendar;

@Entity
public class ClubMember {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Club club;

  // Determine the interactions allowed by the student
  @Enumerated(EnumType.STRING)
  private ClubRole role;

  // Student's position in club, doesn't have any effect
  private String position;

  @ManyToOne
  private Student student;

  @ManyToOne
  private ClubMember parent;

  private Integer fromYear = Calendar.getInstance().get(Calendar.YEAR);

  private Integer toYear = Calendar.getInstance().get(Calendar.YEAR)+1;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() { return student; }

  public void setStudent(Student student) { this.student = student; }

  public ClubRole getRole() { return role; }

  public void setRole(ClubRole role) { this.role = role; }

  public Club getClub() { return club; }

  public void setClub(Club club) { this.club = club; }

  public ClubMember getParent() { return parent; }

  public void setParent(ClubMember parent) { this.parent = parent; }

  public String getPosition() { return position; }

  public void setPosition(String position) { this.position = position; }

  public Integer getFrom() {
    return fromYear;
  }

  public void setFrom(Integer from) {
    this.fromYear = from;
  }

  public Integer getTo() {
    return toYear;
  }

  public void setTo(Integer to) {
    this.toYear = to;
  }
}
