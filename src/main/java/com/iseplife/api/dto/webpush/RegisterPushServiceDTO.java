package com.iseplife.api.dto.webpush;

import lombok.Data;

@Data
public class RegisterPushServiceDTO {
  private String auth, key, endpoint, fingerprint;
}