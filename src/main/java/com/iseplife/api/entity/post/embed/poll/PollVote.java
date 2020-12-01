package com.iseplife.api.entity.post.embed.poll;

import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
public class PollVote {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private Student student;

  @ManyToOne
  private PollChoice answer;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public Long getStudentId() {
    return student.getId();
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public PollChoice getAnswer() {
    return answer;
  }

  public void setAnswer(PollChoice answer) {
    this.answer = answer;
  }
}
