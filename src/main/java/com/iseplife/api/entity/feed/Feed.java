package com.iseplife.api.entity.feed;

import javax.persistence.*;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Feed {

  public Feed(String name) {
    this.name = name;
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(updatable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(updatable = false)
  private FeedType type;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Event event;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Club club;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Group group;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Student student;

  @PrePersist
  private void prePersistListener() {
    if (event != null) {
      type = FeedType.EVENT;
    } else if (group != null) {
      type = FeedType.GROUP;
    } else if (club != null) {
      type = FeedType.CLUB;
    } else if (student != null) {
      type = FeedType.STUDENT;
    }
  }

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
