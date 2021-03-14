package com.iseplife.api.conf.jwt;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
public class JwtRefreshRequest {
  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
