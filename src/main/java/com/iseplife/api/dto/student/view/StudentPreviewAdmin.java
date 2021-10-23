package com.iseplife.api.dto.student.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentPreviewAdmin extends StudentPreview {
  private List<String> roles;
  private boolean isArchived;
}
