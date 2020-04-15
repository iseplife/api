package com.iseplife.api.services.fileHandler;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.exceptions.FileException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class CloudinaryHandler extends FileHandler {
  private final int VIDEO_THRESHOLD_SIZE = 100000000;

  private Cloudinary cloudinary;

  @PostConstruct
  private void initializeCloudinary() {
    cloudinary = new Cloudinary("cloudinary://" + key + ":" + secret + "@" + bucket);
  }

  public String upload(MultipartFile file, String path, Boolean pathContainName) {
    return upload(convertToFile(file), path, pathContainName);
  }

  public String upload(File file, String path, Boolean pathContainName) {
    Map params = ObjectUtils.asMap(
      "resource_type", "TBD",
     "public_id", "my_folder/my_name",
     "folder", path
    );
    String type = (String) params.get("resource_type");

    Map res;
    try {
      if (type != null && type.equals("video") && file.length() > VIDEO_THRESHOLD_SIZE) {
        res = cloudinary.uploader().uploadLarge(file, params);
      } else {
        res = cloudinary.uploader().upload(file, params);
      }
    } catch (IOException e) {
      throw new FileException("Could not upload file to cloudinary: ", e);
    }
    return (String) res.get("public_id");
  }

  public boolean delete(String name) {
    try {
      cloudinary.uploader().destroy(name, Collections.EMPTY_MAP);
      return true;
    } catch (IOException e) {
      LOG.error("could not save delete file " + name, e);
      return false;
    }
  }

  public void delete(String name, Map params) {
    try {
      cloudinary.uploader().destroy(name, params);
    } catch (IOException e) {
      throw new FileException("Couldn't delete file", e);
    }
  }

}
