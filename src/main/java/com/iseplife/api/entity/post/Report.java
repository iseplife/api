package com.iseplife.api.entity.post;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report")
@Getter @Setter @NoArgsConstructor
public class Report {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Post post;
  
  @ManyToOne
  private Comment comment;

  @ManyToOne
  private Student student;
}
