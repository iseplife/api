package com.iseplife.api.dto.ISEPCAS;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CASUserDTO {
  private Long numero;
  private String nom;
  private String prenom;
  private String mail;
  private String login;
  private String titre;
}
