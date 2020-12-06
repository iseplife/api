package com.iseplife.api.dto.thread;

public class CommentDTO {
  private String message;
  private Long asClub;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getAsClub() {
    return asClub;
  }

  public void setAsClub(Long asClub) {
    this.asClub = asClub;
  }
}
