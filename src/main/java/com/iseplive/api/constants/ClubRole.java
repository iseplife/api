package com.iseplive.api.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ClubRole {
  MEMBER(Collections.emptyList()),
  PUBLISHER(Collections.singletonList(MEMBER)),
  ADMIN(Arrays.asList(PUBLISHER, MEMBER)),
  SUPER_ADMIN(Arrays.asList(ADMIN, PUBLISHER, MEMBER));

  private List<ClubRole> legacy;

  ClubRole(List<ClubRole> legacy){
    this.legacy = legacy;
  }

  public boolean is(ClubRole role){
    return role == this ||this.legacy.contains(role);
  }
}
