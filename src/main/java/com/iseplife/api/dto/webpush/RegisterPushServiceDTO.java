package com.iseplife.api.dto.webpush;

import lombok.Data;

@Data
public class RegisterPushServiceDTO {
  private String subscriptionKey, fingerprint;
}