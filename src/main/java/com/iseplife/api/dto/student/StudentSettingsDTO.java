package com.iseplife.api.dto.student;

import lombok.Data;

@Data
public class StudentSettingsDTO {
  private Boolean notification;
  private Boolean recognition;
  private String language;
}
