package com.iseplife.api.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.iseplife.api.constants.AWSBucket;
import com.iseplife.api.services.uploading.FileHandlerInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileHandler implements FileHandlerInterface {
  private AmazonS3 s3client;
  
  @Value("${amazonProperties.accessKey}")
  private String accessKey;

  @Value("${amazonProperties.secretKey}")
  private String secretKey;

  @PostConstruct
  private void initializeAmazonClient() {
    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
    s3client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(Regions.EU_WEST_1)
      .build();
  }

  private File convertToFile(MultipartFile file) throws IOException {
    File convertedFile = new File(file.getOriginalFilename());
    FileOutputStream fos = new FileOutputStream(convertedFile);
    fos.write(file.getBytes());
    fos.close();
    return convertedFile;
  }


  public String upload(MultipartFile file, String fileURL, AWSBucket bucket) {
    try {
      return upload(convertToFile(file), fileURL, bucket);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String upload(File file, String fileURL, AWSBucket bucket) {
    s3client.putObject(
      bucket.name(),
      fileURL,
      file
    );
    return fileURL;
  }

  @Override
  public Boolean delete(String fileURL) {
    return null;
  }

  private String datePath() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    return dateFormat.format(new Date()) + "/";
  }


}
