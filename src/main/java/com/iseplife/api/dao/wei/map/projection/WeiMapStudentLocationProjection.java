package com.iseplife.api.dao.wei.map.projection;

import java.util.Date;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public interface WeiMapStudentLocationProjection {
  Date getTimestamp();
  StudentPreviewProjection getStudent();
  Double getLat();
  Double getLng();
}
