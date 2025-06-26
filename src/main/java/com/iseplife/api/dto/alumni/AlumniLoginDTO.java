package com.iseplife.api.dto.alumni;

import lombok.Data;

@Data
public class AlumniLoginDTO {
  private Long studentId;
  private String password;
}
