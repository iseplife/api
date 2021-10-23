package com.iseplife.api.dto.group.view;

import com.iseplife.api.dto.student.view.StudentPreview;
import lombok.Data;

@Data
public class GroupMemberView {
  private Long id;
  private StudentPreview student;
  private boolean admin;
}
