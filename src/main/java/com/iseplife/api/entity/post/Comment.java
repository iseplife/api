package com.iseplife.api.entity.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "thread_comment")
@Getter @Setter @NoArgsConstructor
public class Comment implements ThreadInterface {
  @Id
  @GeneratedValue
  private Long id;

  @Column(length = 500)
  private String message;

  private Date creation = new Date();

  private Date lastEdition;

  @ManyToOne
  private Thread parentThread;

  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @ManyToOne
  private Student student;

  @ManyToOne
  private Club asClub;
}
