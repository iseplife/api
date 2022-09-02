package com.iseplife.api.dto.post.view;

import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.dto.thread.view.CommentView;

import lombok.Data;

@Data
public class ReportView {
  StudentPreview student;
  PostView post;
  CommentView comment;
  Long id;
}
