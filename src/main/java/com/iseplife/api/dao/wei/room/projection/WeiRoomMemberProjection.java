package com.iseplife.api.dao.wei.room.projection;

import java.util.Date;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public interface WeiRoomMemberProjection {
  StudentPreviewProjection getStudent();
  Boolean getAdmin();
  Date getJoined();
}
