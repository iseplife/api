package com.iseplive.api.dto.view;

/**
 * Created by Guillaume on 08/12/2017.
 * back
 */
public class ImportStudentResultView {
  private Integer alreadyImported = 0;
  private Integer studentAndPhotoNotMatched = 0;
  private Integer imported = 0;
  private Integer photoAdded = 0;
  private Integer photosSent = 0;
  private Integer studentsSent = 0;

  public void incrImport() {
    imported++;
  }

  public void incrPhotoAdded() {
    photoAdded++;
  }

  public void incrAlreadyImported() {
    alreadyImported++;
  }

  public void incrStudentPhotoNotMatched() {
    studentAndPhotoNotMatched++;
  }

  public Integer getAlreadyImported() {
    return alreadyImported;
  }

  public void setAlreadyImported(Integer alreadyImported) {
    this.alreadyImported = alreadyImported;
  }

  public Integer getImported() {
    return imported;
  }

  public void setImported(Integer imported) {
    this.imported = imported;
  }

  public Integer getStudentAndPhotoNotMatched() {
    return studentAndPhotoNotMatched;
  }

  public void setStudentAndPhotoNotMatched(Integer studentAndPhotoNotMatched) {
    this.studentAndPhotoNotMatched = studentAndPhotoNotMatched;
  }

  public Integer getPhotoAdded() {
    return photoAdded;
  }

  public void setPhotoAdded(Integer photoAdded) {
    this.photoAdded = photoAdded;
  }

  public Integer getPhotosSent() {
    return photosSent;
  }

  public void setPhotosSent(Integer photosSent) {
    this.photosSent = photosSent;
  }

  public Integer getStudentsSent() {
    return studentsSent;
  }

  public void setStudentsSent(Integer studentsSent) {
    this.studentsSent = studentsSent;
  }
}
