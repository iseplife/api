package com.iseplife.api.dto;

import lombok.Data;

@Data
public class CASAuthentificationDTO {
  private String id = null;
  private Integer result;
  private Integer error;
}
