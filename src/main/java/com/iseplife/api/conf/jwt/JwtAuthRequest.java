package com.iseplife.api.conf.jwt;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
public class JwtAuthRequest {
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
