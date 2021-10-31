package com.iseplife.api.entity.club;

import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.ClubRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Getter @Setter @NoArgsConstructor
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

  // Starting year of the first x school session the member has been part of the club
  private Integer fromYear;

  // Starting year of the last school session the member has been part of the club
  private Integer toYear;

}
