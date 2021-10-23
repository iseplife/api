package com.iseplife.api.entity.post.embed.poll;

import com.iseplife.api.entity.user.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class PollVote {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private Student student;

  @ManyToOne
  private PollChoice choice;
}
