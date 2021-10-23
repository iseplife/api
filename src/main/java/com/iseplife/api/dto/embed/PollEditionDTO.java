package com.iseplife.api.dto.embed;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PollEditionDTO {
  private Long id;
  private String title;
  private List<PollChoiceDTO> choices;
  private Date endsAt;
  private boolean multiple;
  private boolean anonymous;
}
