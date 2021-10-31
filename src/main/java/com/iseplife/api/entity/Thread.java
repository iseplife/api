package com.iseplife.api.entity;

import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter @NoArgsConstructor
public class Thread {

  public Thread(ThreadType type){
    this.type = type;
  }

  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  ThreadType type;

  @OneToOne(mappedBy = "thread", cascade = CascadeType.ALL)
  private Post post;

  @OneToOne(mappedBy = "thread", cascade = CascadeType.ALL)
  private Comment comment;

  @OneToMany(mappedBy = "parentThread", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  public Feed getFeed() {
    if (post != null) {
      return post.getFeed();
    } else {
      return comment.getParentThread().getFeed();
    }
  }
}
