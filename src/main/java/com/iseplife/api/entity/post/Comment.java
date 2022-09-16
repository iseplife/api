package com.iseplife.api.entity.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.services.ThreadService;
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

  @Column(length = ThreadService.MAX_COMMENT_LENGTH)
  private String message;

  private Date creation = new Date();

  private Date lastEdition;

  @ManyToOne(fetch = FetchType.LAZY)
  private Thread parentThread;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Thread thread;

  @ManyToOne
  private Student student;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment", cascade = CascadeType.ALL)
  private List<Report> reports;

  @ManyToOne
  private Club asClub;
}
