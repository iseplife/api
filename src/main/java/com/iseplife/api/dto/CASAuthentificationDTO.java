package com.iseplife.api.dto;

public class CASAuthentificationDTO {

  private String id = null;

  private Integer result;
  private Integer error;


  public Integer getResult() {
    return result;
  }

  public void setResult(Integer result) {
    this.result = result;
  }

  public Integer getError() {
    return error;
  }

  public void setError(Integer error) {
    this.error = error;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
