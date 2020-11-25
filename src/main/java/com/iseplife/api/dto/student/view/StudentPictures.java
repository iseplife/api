package com.iseplife.api.dto.student.view;

public class StudentPictures {
  private String original;
  private String custom;

  public StudentPictures(String original, String custom) {
    this.original = original;
    this.custom = custom;
  }


  public String getOriginal() {
    return original;
  }

  public void setOriginal(String original) {
    this.original = original;
  }

  public String getCustom() {
    return custom;
  }

  public void setCustom(String custom) {
    this.custom = custom;
  }
}
