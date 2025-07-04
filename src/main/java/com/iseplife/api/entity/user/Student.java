package com.iseplife.api.entity.user;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.constants.FamilyType;
import com.iseplife.api.constants.Language;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.entity.subscription.FirebaseSubscription;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Student implements UserDetails, Feedable, Author, Subscribable {
  @Id
  @Column(unique = true)
  private Long id;

  private Integer promo;
  private Date archivedAt;
  private Date lastConnection;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private Boolean recognition = false;
  private Boolean notification = false;
  private Language language = Language.FR;

  private Integer mediaCounter;
  private Date mediaCooldown;

  @NotNull
  private Boolean hasDefaultPicture = false;
  
  private Boolean didFirstFollow = false;

  private String picture;
  
  private Date lastExploreWatch = new Date(0);
  
  private FamilyType family;

  @ManyToMany(fetch = FetchType.LAZY)
  private Set<Role> roles;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Feed feed;

  @OneToMany(mappedBy="owner", fetch = FetchType.LAZY)
  private Set<FirebaseSubscription> firebaseSubscriptions;
  
  @OneToMany(mappedBy="student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<PollVote> pollVotes;
  
  @OneToMany(mappedBy="author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Post> posts;

  @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Subscription> subscriptions;

  @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
  private List<Notification> notifications;
  

  private String password;

  @Override
  public String getName() {
    return firstName + " " + lastName;
  }

  @Override
  public AuthorType getAuthorType() {
    return AuthorType.STUDENT;
  }

  @Override
  public String getThumbnail() {
    return picture;
  }


  public boolean isArchived() { return !(archivedAt == null || archivedAt.getTime() > new Date().getTime()); }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public int hashCode() {
    return getId().intValue();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Student)
      return ((Student) obj).getId() == getId();

    return super.equals(obj);
  }
}
