package com.iseplife.api.dto.view;

import java.util.Date;

import com.iseplife.api.constants.SearchItem;
import lombok.Data;

@Data
public class SearchItemView {
  private Long id;
  private SearchItem type;
  private String name;
  private String thumbURL;
  private String description;
  private Boolean status; // Event passed, club archived, student archived
  private Date startsAt;
}
