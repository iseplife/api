package com.iseplife.api.dto.event;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class EventDTO {
  private String type;
  private String title;
  private String description;
  private String location;
  private Float[] coordinates;
  private Date startsAt;
  private Date endsAt;
  private Long club;
  private Long previousEditionId;
  private String ticketUrl;
  private Float price;
  private Date published;
  private boolean closed;
  private Set<Long> targets;
}
