package com.iseplife.api.services.fileHandler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
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
  public String upload(MultipartFile file, String path, Map params) {
    return upload(convertToFile(file), path, params);
  }

  @Override
  public String upload(File file, String path, Map params) {
    String fullName = path + "/" + generateRandomName(file);
    s3client.putObject(
      bucket,
      fullName,
      file
    );

    return fullName;
  }

  @Override
  public boolean delete(String path) {
    s3client.deleteObject(bucket, path);
    return true;
  }

  public void delete(String[] path) {
    s3client.deleteObjects(
      new DeleteObjectsRequest(bucket).withKeys(path)
    );
  }
}
