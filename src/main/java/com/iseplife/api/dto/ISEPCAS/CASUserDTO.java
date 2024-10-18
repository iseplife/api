package com.iseplife.api.dto.ISEPCAS;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class CASUserDTO {
  private Long numero;
  private String nom;
  private String prenom;
  private String mail;
  private String login;
  private String titre;
}
