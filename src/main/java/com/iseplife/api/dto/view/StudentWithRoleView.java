package com.iseplife.api.dto.view;

import com.iseplife.api.dto.student.StudentUpdateAdminDTO;

import java.util.List;

/**
 * Created by Guillaume on 01/01/2018.
 * back
 */
public class StudentWithRoleView extends StudentUpdateAdminDTO {

  private Long id;

  private String photoUrl;
  private String photoUrlThumb;
  private boolean archived;

  private List<String> rolesValues;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public String getPhotoUrlThumb() {
    return photoUrlThumb;
  }

  public void setPhotoUrlThumb(String photoUrlThumb) {
    this.photoUrlThumb = photoUrlThumb;
  }

  public List<String> getRolesValues() {
    return rolesValues;
  }

  public void setRolesValues(List<String> rolesValues) {
    this.rolesValues = rolesValues;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }
}
