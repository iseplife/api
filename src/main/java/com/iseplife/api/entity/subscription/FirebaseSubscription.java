package com.iseplife.api.entity.subscription;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FirebaseSubscription {

  @Id
  @GeneratedValue
  private Long id;

  private String token;
  private String fingerprint;

  @CreationTimestamp
  private Date lastUpdate;

  @ManyToOne(fetch = FetchType.LAZY)
  private Student owner;
}