package com.iseplife.api.services.fileHandler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;

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
    return delete(fullPath, false);
  }

  @Override
  public boolean delete(String fullPath, Boolean clean) {
    s3client.deleteObject(bucket, fullPath);

    // Clean deletion search for all thumbnail of images
    // CAREFUL ! This process can be very long when a lot of images are present in the bucket
    if (clean) {
      int pivot = fullPath.lastIndexOf("/");
      String path = fullPath.substring(0, pivot);
      ObjectListing listing = s3client.listObjects(bucket, path);
      listing.getObjectSummaries().forEach(o -> {
        if (o.getKey().endsWith(fullPath.substring(pivot + 1))) {
          s3client.deleteObject(bucket, o.getKey());
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
