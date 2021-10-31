package com.iseplife.api.dto.post;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class PostUpdateDTO {
  private String description;
  private Date publicationDate;
  private Long linkedClub;
  private Boolean removeEmbed = false;
  private Map<String, Long> attachements = new HashMap<>();
}
