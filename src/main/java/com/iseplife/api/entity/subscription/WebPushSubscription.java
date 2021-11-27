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

import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.hibernate.annotations.CreationTimestamp;

import com.iseplife.api.entity.user.Student;

@Entity
@Getter @Setter
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
