package com.iseplive.api.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum ClubRole {
  MEMBER(Collections.emptyList()),
  PUBLISHER(Collections.singletonList(MEMBER)),
  ADMIN(Arrays.asList(PUBLISHER, MEMBER)),
  SUPER_ADMIN(Arrays.asList(ADMIN, PUBLISHER, MEMBER));

  private List<ClubRole> legacy;

  ClubRole(List<ClubRole> legacy) { this.legacy = legacy;
  }

  public List<ClubRole> getLegacy() { return legacy; }

  public List<ClubRole> getParent() {
    return Arrays
      .stream(ClubRole.values())
      .filter(role -> role.is(this))
      .collect(Collectors.toList());
  }

  public boolean is(ClubRole role) {
    return role == this || this.legacy.contains(role);
  }
}
