package com.iseplife.api.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.exceptions.FileException;
import com.iseplife.api.utils.MediaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class FileHandler {
  private final Logger LOG = LoggerFactory.getLogger(FileHandler.class);
  private final int VIDEO_THRESHOLD_SIZE = 100000000;

  private Cloudinary cloudinary;

  @Value("${cloudinary.api_key}")
  private String key;

  @Value("${cloudinary.api_secret}")
  private String secret;

  @Value("${cloudinary.cloud_name}")
  private String cloud;

  @PostConstruct
  private void initializeCloudinary() {
    cloudinary = new Cloudinary("cloudinary://" + key + ":" + secret + "@" + cloud);
  }


  public String upload(MultipartFile file, Map params) {
    return upload(convertToFile(file), params);
  }

  public String upload(File file, Map params) {
    String type = (String) params.get("resource_type");
    Map res;
    try {
      if( type != null  && type.equals("video") && file.length() > VIDEO_THRESHOLD_SIZE){
        res = cloudinary.uploader().uploadLarge(file, params);
      }else {
        res = cloudinary.uploader().upload(file, params);
      }
    } catch (IOException e) {
      throw new FileException("Could not upload file to cloudinary: ", e);
    }
    return (String) res.get("public_id");
  }

  private File convertToFile(MultipartFile file) {
    File convertedFile;
    try {
      convertedFile = new File(file.getOriginalFilename());
      FileOutputStream fos = new FileOutputStream(convertedFile);
      fos.write(file.getBytes());
      fos.close();
    } catch (IOException e) {
      LOG.error("could not save file", e);
      throw new FileException("could not create file: ", e);
    }
    return convertedFile;
  }

  public void delete(String name, Map params) {
    try {
      cloudinary.uploader().destroy(name, params);
    } catch (IOException e) {
      LOG.error("could not save delete file " + name, e);
      throw new FileException("Couldn't delete file", e);
    }
  }
}
