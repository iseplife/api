package com.iseplife.api.entity.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

@Entity
@Table(name = "thread_like")
public class Like {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Thread thread;

  @ManyToOne
  private Student student;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public Thread getThread() {
    return thread;
  }

  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public Student getStudent() { return student; }

  public void setStudent(Student student) { this.student = student; }
}
