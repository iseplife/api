package com.iseplife.api.dto.post;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class PostCreationDTO {
  private Long feed = null;
  private String description;
  private Date publicationDate = new Date();
  private Long linkedClub = null;
  private Map<String, Long> attachements = new HashMap<>();
  private boolean draft = false;
}
