package com.iseplife.api.entity.group;

import com.iseplife.api.constants.GroupType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.feed.Feedable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="groups")
@Getter @Setter @NoArgsConstructor
public class Group implements Feedable {
  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private GroupType type = GroupType.DEFAULT;

  @Column(unique = true)
  private String name;

  private boolean restricted = false;
  private Date archivedAt;
  private String cover;

  @OneToOne(cascade = CascadeType.ALL)
  private Feed feed;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupMember> members;

  public Boolean isArchived() {
    return !(archivedAt == null || archivedAt.getTime() > new Date().getTime());
  }

}
