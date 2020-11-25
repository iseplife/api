package com.iseplife.api.dto.student;

public class StudentSettingsDTO {
  private Boolean notification;
  private Boolean recognition;
  private String language;

  public Boolean getNotification() {
    return notification;
  }

  public void setNotification(Boolean notification) {
    this.notification = notification;
  }

  public Boolean getRecognition() {
    return recognition;
  }

  public void setRecognition(Boolean recognition) {
    this.recognition = recognition;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
