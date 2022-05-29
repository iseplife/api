package com.iseplife.api.dto.student.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoggedStudentPreview extends StudentPreview {
  protected Long unwatchedNotifications;
  protected Long totalNotifications;
  protected Boolean didFirstFollow;
}
