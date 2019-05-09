package com.iseplive.api.dto.dor;

public class DorConfigDTO {
  private DorConfigAttributeDTO titre;
  private DorConfigAttributeDTO name;
  private DorConfigAttributeDTO birthdate;

  public DorConfigAttributeDTO getTitre() {
    return titre;
  }

  public void setTitre(DorConfigAttributeDTO titre) {
    this.titre = titre;
  }

  public DorConfigAttributeDTO getName() {
    return name;
  }

  public void setName(DorConfigAttributeDTO name) {
    this.name = name;
  }

  public DorConfigAttributeDTO getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(DorConfigAttributeDTO birthdate) {
    this.birthdate = birthdate;
  }
}
