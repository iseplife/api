package com.iseplife.api.services.fileHandler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.iseplife.api.conf.StorageConfig;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AmazonHandler extends FileHandler {

  private AmazonS3 s3client;

  @PostConstruct
  private void initializeAWS() {
    AWSCredentials credentials = new BasicAWSCredentials(key, secret);

    s3client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion("eu-west-3")
      .build();
  }

  @Override
  public String upload(MultipartFile file, String path, Boolean pathContainName, Map metadata) {
    return upload(convertToFile(file), path, pathContainName, metadata);
  }

  @Override
  public String upload(File file, String path, Boolean pathContainName, Map metadata) {
    String completePath = pathContainName ? path : path + "/" + generateRandomName(file);
    PutObjectRequest request = new PutObjectRequest(bucket, completePath, file);

    ObjectMetadata m = new ObjectMetadata();
    metadata.forEach((k, v) -> m.addUserMetadata(k.toString(), v.toString()));
    request.setMetadata(m);

    s3client.putObject(request);
    return completePath;
  }

  @Override
  public boolean delete(String fullPath) {
    return delete(fullPath, true);
  }

  @Override
  public boolean delete(String key, Boolean clean) {
    s3client.deleteObject(bucket, key);

    // Clean deletion search for generated thumbnails
    if (clean) {
      CompletableFuture.runAsync(() -> {
        int index = key.lastIndexOf("/");
        String rootPath = key.substring(0, index);
        String filename = key.substring(index+1);

        boolean containSize = rootPath.matches("/(?!autoxauto)(\\d+|auto)x(\\d+|auto)$");
        if (!containSize) {
          for (Map.Entry<String, StorageConfig.MediaConf> entry : StorageConfig.MEDIAS_CONF.entrySet()) {
            if (rootPath.equals(entry.getValue().path)) {
              String[] sizes = entry.getValue().sizes.split(";");
              for (String size : sizes) {
                s3client.deleteObject(bucket, rootPath + "/" + size + "/" + filename);
              }
              break;
            }
          }
        }


      });
    }
    return true;
  }

  public void delete(String[] path) {
    s3client.deleteObjects(
      new DeleteObjectsRequest(bucket).withKeys(path)
    );
  }
}
