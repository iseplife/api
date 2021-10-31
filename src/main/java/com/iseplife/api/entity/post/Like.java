package com.iseplife.api.entity.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.user.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "thread_like")
@Getter @Setter @NoArgsConstructor
public class Like {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Thread thread;

  @ManyToOne
  private Student student;
}
