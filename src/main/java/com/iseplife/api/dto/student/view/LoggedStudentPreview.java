package com.iseplife.api.dto.student.view;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoggedStudentPreview extends StudentPreview {
  protected Long unwatchedNotifications;
  protected Long totalNotifications;
  protected Boolean didFirstFollow;
  protected Date lastExploreWatch;
  protected Boolean passwordSetup;
}
