package com.iseplife.api.services.fileHandler;

import com.iseplife.api.exceptions.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public abstract class FileHandler {
  final protected Logger LOG = LoggerFactory.getLogger(FileHandler.class);

  @Value("${storage.api-key}")
  protected String key;

  @Value("${storage.api-secret}")
  protected String secret;

  @Value("${storage.bucket}")
  protected String bucket;

  public abstract String upload(MultipartFile file, String path, Boolean pathContainName, Map metadata);

  public abstract String upload(File file, String path, Boolean pathContainName, Map metadata);

  public abstract boolean delete(String name, Boolean clean);
  public abstract boolean delete(String name);

  public File convertToFile(MultipartFile file) {
    File tempFile;
    try {
      tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);

    } catch (IOException e) {
      LOG.error("could not save file", e);
      throw new FileException("could not create file: ", e);
    }
    return tempFile;
  }

  protected String generateRandomName(File file, int length) {
    String random = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int pos = (int) (Math.random() * random.length());
      out.append(random.charAt(pos));
    }
    return out + "." + getFileExtension(file.getName());
  }

  public String getFileExtension(String filename) {
    int index = filename.lastIndexOf(".");
    return index != -1 ? filename.substring(index + 1) : "";
  }

  protected String generateRandomName(File file) {
    return generateRandomName(file, 30);
  }
}
