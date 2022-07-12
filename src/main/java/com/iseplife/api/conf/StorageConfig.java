package com.iseplife.api.conf;

import com.iseplife.api.services.fileHandler.AmazonHandler;
import com.iseplife.api.services.fileHandler.CloudinaryHandler;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;


@Configuration
public class StorageConfig implements WebMvcConfigurer {

  public static final String GALLERY_SIZES = "autox140/70;autox500/75;1920xauto/82;5120xauto/88";
  public static final String POST_SIZES = "autox500/72;1920xauto";
  public static final String AVATAR_SIZES = "300x300;200x200;90x90/75";
  public static final String COVER_SIZES = "1920xauto";

  public static class MediaConf {
    final public String path;
    final public String sizes;
    MediaConf(String p, String s) {
      sizes = s;
      path = p;
    }
  }
  public static final Map<String, MediaConf> MEDIAS_CONF = Map.of(
    "video", new MediaConf("vid", null),
    "document", new MediaConf("doc", null),
    "post", new MediaConf("img", POST_SIZES),
    "user_avatar", new MediaConf("img/usr", AVATAR_SIZES),
    "user_original", new MediaConf("img/usr/org",AVATAR_SIZES),
    "club_avatar", new MediaConf("img/clb", AVATAR_SIZES),
    "club_cover", new MediaConf("img/clb/cvr", COVER_SIZES),
    "feed_cover", new MediaConf("img/" + COVER_SIZES, COVER_SIZES),
    "gallery", new MediaConf("img/g", GALLERY_SIZES)
  );


  @Bean("FileHandlerBean")
  @ConditionalOnProperty(
    name = "storage.name",
    havingValue = "cloudinary")
  public FileHandler setCloudinaryAsHandler() {
    return new CloudinaryHandler();
  }

  @Bean("FileHandlerBean")
  @ConditionalOnProperty(
    name = "storage.name",
    havingValue = "aws")
  public FileHandler setAWSAsHandler() {
    return new AmazonHandler();
  }

}
