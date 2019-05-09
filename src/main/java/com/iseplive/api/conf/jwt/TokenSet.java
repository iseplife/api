package com.iseplive.api.conf.jwt;

/**
 * Created by Guillaume on 17/10/2017.
 * back
 */
public class TokenSet {
  private String token;
  private String refreshToken;

  TokenSet(String token, String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
