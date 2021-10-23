package com.iseplife.api.dto;

import lombok.Data;

@Data
public class CASUserDTO {
  private Long numero;
  private String nom;
  private String prenom;
  private String mail;
  private String login;
  private String titre;
}
