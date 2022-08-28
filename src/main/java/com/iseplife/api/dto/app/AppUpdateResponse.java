package com.iseplife.api.dto.app;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AppUpdateResponse {
  private final String version, url;
}
