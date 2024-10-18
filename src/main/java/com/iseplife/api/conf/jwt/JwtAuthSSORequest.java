package com.iseplife.api.conf.jwt;

import lombok.Data;

@Data
public class JwtAuthSSORequest {
  private String ticket;
  private String service;
}
