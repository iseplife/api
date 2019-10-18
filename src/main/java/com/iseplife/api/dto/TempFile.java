package com.iseplife.api.dto;

import java.io.File;

/**
 * Created by Guillaume on 12/02/2018.
 * back
 */
public class TempFile {
  private String contentType;
  private File file;

  public TempFile(String contentType, File file) {
    this.contentType = contentType;
    this.file = file;
  }

  public String getContentType() {
    return contentType;
  }

  public File getFile() {
    return file;
  }
}
