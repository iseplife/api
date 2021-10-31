package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.entity.user.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Matched {
  @Id
  @GeneratedValue
  private Long id;

  @OneToOne
  private Student match;

  @OneToOne
  private Student owner;

  @ManyToOne
  private Image image;
}
