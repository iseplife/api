package com.iseplife.api.constants;

public enum GroupType {
  ADMIN_ASSOCIATION ("Groupe des dictateurs"),
  ASSOCIATION_LIFE ("Vie associative"),
  ADMIN ("Administrateurs"),
  DEFAULT("default");

  GroupType(String name){
    this.name = name;
  }

  private String name;

  public String getName(){
    return name;
  }
}
