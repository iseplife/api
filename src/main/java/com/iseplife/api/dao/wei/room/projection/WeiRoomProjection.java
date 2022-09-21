package com.iseplife.api.dao.wei.room.projection;

import java.util.Date;
import java.util.List;

public interface WeiRoomProjection {
  String getId();
  Integer getCapacity();
  Date getReservedUpTo();
  Boolean getBooked();
  List<WeiRoomMemberProjection> getMembers();
}
