package com.iseplife.api.entity.subscription;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.hibernate.annotations.CreationTimestamp;

import com.iseplife.api.entity.user.Student;

@Entity
public class WebPushSubscription {

  @Id
  @GeneratedValue
  private Long id;

  private String auth;
  private String key;
  @Column(length = 512)
  private String endpoint;
  @Column(unique = true)
  private String fingerprint;
  
  @CreationTimestamp
  private Date lastUpdate;

  @ManyToOne(fetch = FetchType.LAZY)
  private Student owner;
  
  public Long getId() {
    return id;
  }
  
  public Date getLastUpdate() {
    return lastUpdate;
  }
  
  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }
  
  public void setOwner(Student owner) {
    this.owner = owner;
  }

  public Student getOwner() {
    return owner;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }

  public String getAuth() {
    return auth;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getEndpoint() {
    return endpoint;
  }
  
  public void setFingerprint(String fingerprint) {
    this.fingerprint = fingerprint;
  }
  
  public String getFingerprint() {
    return fingerprint;
  }

  public byte[] getAuthAsBytes() {
    return Base64.getDecoder().decode(getAuth());
  }

  public byte[] getKeyAsBytes() {
    return Base64.getDecoder().decode(getKey());
  }
  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
        Security.addProvider(new BouncyCastleProvider());
    }
  }

  public PublicKey getUserPublicKey()
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
    ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
    ECPoint point = ecSpec.getCurve().decodePoint(getKeyAsBytes());
    ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);

    return kf.generatePublic(pubSpec);
  }

}
