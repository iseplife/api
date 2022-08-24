package com.iseplife.api.entity.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.rich.RichLink;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PostState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Post implements ThreadInterface {
  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private PostState state;

  @Column(columnDefinition = "TEXT")
  private String description;

  private Date publicationDate;
  private Date creationDate; //TODO: remove creation date ? Not useful anymore
  private boolean pinned = false;
  private boolean homepageForced = false;
  private boolean homepagePinned = false;

  @Any(
    fetch = FetchType.EAGER,
    metaColumn = @Column(name = "embed_type")
  )
  @AnyMetaDef(
    idType = "long",
    metaType = "string",
    metaValues = {
      @MetaValue(value = EmbedType.GALLERY, targetEntity = Gallery.class),
      @MetaValue(value = EmbedType.POLL, targetEntity = Poll.class),
      @MetaValue(value = EmbedType.DOCUMENT, targetEntity = Document.class),
      @MetaValue(value = EmbedType.VIDEO, targetEntity = Video.class),
      @MetaValue(value = EmbedType.IMAGE, targetEntity = Image.class),
      @MetaValue(value = EmbedType.RICH_LINK, targetEntity = RichLink.class),
    }
  )
  @JoinColumn(name = "embed_id")
  private Embedable embed;

  @ManyToOne
  private Student author;

  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @ManyToOne
  private Club linkedClub = null;

  @ManyToOne
  private Feed feed;
}
