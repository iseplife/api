package com.iseplife.api.dto.webpush;

public class RegisterPushServiceDTO {

  private String auth;
  private String key;
  private String endpoint;
  private String fingerprint;

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
}