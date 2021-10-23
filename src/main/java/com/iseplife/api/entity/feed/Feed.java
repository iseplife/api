package com.iseplife.api.entity.feed;

import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Gallery;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Feed implements Subscribable {

  public Feed(String name) {
    this.name = name;
  }

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Event event;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Club club;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Group group;

  @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Subscription> subscriptions;
}
