package com.iseplife.api.utils;

import com.iseplife.api.conf.StorageConfig;

public class MediaUtils {

  public static Boolean isOriginalPicture(String path){
    return path != null && path.contains(StorageConfig.MEDIAS_CONF.get("user_original").path);
  }

  public static String extractFilename(String path){
    return path.substring(path.lastIndexOf("/") +1);
  }
}
