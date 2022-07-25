package com.iseplife.api.entity.feed;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Feed {

  public Feed(String name, FeedType type) {
    this.name = name;
    this.type = type;
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(updatable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(updatable = false)
  private FeedType type;

  @Where(clause = "pseudo = false")
  @OneToMany(mappedBy = "feed", orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Gallery> galleries;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Event event;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Club club;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Group group;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Student student;

  public Feedable getFeedContext() {
    switch (type) {
      case EVENT:
        return event;
      case STUDENT:
        return student;
      case CLUB:
        return club;
      case GROUP:
        return group;
      default:
        return null;
    }
  }

}
