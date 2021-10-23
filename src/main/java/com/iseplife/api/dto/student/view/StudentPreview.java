package com.iseplife.api.dto.student.view;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;
import lombok.Data;

@Data
public class StudentPreview implements StudentPreviewProjection {
  protected Long id;
  protected String firstName;
  protected String lastName;
  protected Integer promo;
  protected String picture;
}
