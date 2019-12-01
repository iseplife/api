package com.iseplife.api.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.media.Embed;
import com.iseplife.api.entity.media.Image;
import com.iseplife.api.entity.media.Video;
import com.iseplife.api.entity.post.embed.Document;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.entity.Feed;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;

@Entity
@AnyMetaDef(
  name = "postEmbed",
  metaType = "string",
  idType = "int",
  metaValues = {
    @MetaValue(value = EmbedType.GALLERY, targetEntity = Gallery.class),
    @MetaValue(value = EmbedType.POLL, targetEntity = Poll.class),
    @MetaValue(value = EmbedType.DOCUMENT, targetEntity = Document.class),
    @MetaValue(value = EmbedType.VIDEO, targetEntity = Video.class),
    @MetaValue(value = EmbedType.IMAGE, targetEntity = Image.class)
  }
)
public class Post implements ThreadInterface {

  @Id
  @GeneratedValue
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;

  private Date publicationDate;
  private Date creationDate; //TODO: remove creation date ? Not useful anymore
  private Boolean isPrivate = false;
  private Boolean isPinned = false;

  @Any(
    metaDef = "postEmbed",
    metaColumn = @Column(name = "embed_type"))
  @JoinColumn(name = "embed_id")
  private Embed embed;

  @ManyToOne
  private Student author;

  @JsonIgnore
  @OneToOne()
  private Thread thread;

  @ManyToOne
  private Club linkedClub = null;

  @JsonIgnore
  @ManyToOne
  private Feed feed;

  @Enumerated(EnumType.STRING)
  private PublishStateEnum publishState;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Student getAuthor() {
    return author;
  }

  public void setAuthor(Student author) {
    this.author = author;
  }

  public PublishStateEnum getPublishState() {
    return publishState;
  }

  public void setPublishState(PublishStateEnum publishState) {
    this.publishState = publishState;
  }

  public Embed getEmbed() {
    return embed;
  }

  public void setEmbed(Embed embed) {
    this.embed = embed;
  }

  @JsonIgnore
  public List<Comment> getComments() {
    return thread.getComments();
  }

  @JsonIgnore
  public List<Like> getLikes() {
    return thread.getLikes();
  }

  public Boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public Boolean getPinned() {
    return isPinned;
  }

  public void setPinned(Boolean pinned) {
    isPinned = pinned;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public Club getLinkedClub() {
    return linkedClub;
  }

  public void setLinkedClub(Club linkedClub) {
    this.linkedClub = linkedClub;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  @Override
  public Thread getThread() {
    return thread;
  }

  @Override
  public void setThread(Thread thread) {
    this.thread = thread;
  }
}
