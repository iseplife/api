package com.iseplife.api.services.uploading;

import com.iseplife.api.constants.AWSBucket;

import java.io.File;
import java.io.IOException;

public interface FileHandlerInterface {

  String upload(File file, String fileURL, AWSBucket bucket) throws IOException;

  Boolean delete(String fileURL);

}
