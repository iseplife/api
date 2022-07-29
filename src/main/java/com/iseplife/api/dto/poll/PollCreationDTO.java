package com.iseplife.api.dto.poll;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PollCreationDTO {
  private List<PollChoiceDTO> choices;
  private Date endsAt;
  private boolean multiple;
  private boolean anonymous;
}
