package com.iseplife.api.utils;

import com.iseplife.api.exceptions.FileException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Component
public class MediaUtils {

  private final Logger LOG = LoggerFactory.getLogger(MediaUtils.class);

  @Value("${storage.url}")
  private String baseUrl;

  private static final String publicBaseUrl = "/storage";

  public String resolvePath(String dir, String name, boolean thumb) {
    if (thumb) {
      return String.format("%s/%s_thumb", dir, sanitizePath(name));
    }
    return String.format("%s/%s", dir, sanitizePath(name));
  }

  public String resolvePath(String dir, String name, boolean thumb, Date date) {
    return resolvePath(pathGroupByDate(date, dir), name, thumb);
  }

  public String resolvePath(String dir, String name, boolean thumb, Long studentId) {
    return resolvePath(pathGroupByStudentId(studentId, dir), name, thumb);
  }

  private String pathGroupByStudentId(Long studentId, String dir) {
    String studentIdStr = Long.toString(studentId);
    return String.format(
      "%s/%s",
      dir,
      studentIdStr.substring(0, studentIdStr.length() - 2)
    );
  }

  private String pathGroupByDate(Date date, String dir) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return String.format(
      "%s/%d/%d/%d",
      dir,
      c.get(Calendar.YEAR),
      c.get(Calendar.MONTH) + 1,
      c.get(Calendar.DATE)
    );
  }

  public String getPublicUrlImage(String path) {
    return publicBaseUrl + path + ".jpg";
  }

  public String getPublicUrl(String path) {
    return publicBaseUrl + path;
  }

  private String randomName(int length) {
    String random = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int pos = (int) (Math.random() * random.length());
      out.append(random.charAt(pos));
    }
    return out.toString();
  }

  public String randomName() {
    return randomName(30);
  }

  public void removeIfExistPublic(String publicPath) {
    Path p = Paths.get(baseUrl + publicPath.replaceFirst(publicBaseUrl, ""));
    if (p.toFile().exists()) {
      try {
        Files.delete(p);
      } catch (IOException e) {
        LOG.error("could not remove file", e);
        throw new FileException("could not delete file: " + p, e);
      }
    }
  }

  public void removeIfExistJPEG(String path) {
    Path p = Paths.get(baseUrl + path + ".jpg");
    if (p.toFile().exists()) {
      try {
        Files.delete(p);
      } catch (IOException e) {
        throw new FileException("could not delete file: " + p, e);
      }
    }
  }

  public void removeIfExist(String path) {
    Path p = Paths.get(baseUrl + path);
    if (p.toFile().exists()) {
      try {
        Files.delete(p);
      } catch (IOException e) {
        LOG.error("could not remove file", e);
        throw new FileException("could not delete file: " + p, e);
      }
    }
  }

  /**
   * saveFile saves the file to the path specified
   * and creates new directories if needed.
   *
   * @param filePath
   * @param file
   */
  public File saveFile(String filePath, MultipartFile file) {
    try {
      Path path = Paths.get(getPath(filePath));
      Files.createDirectories(path.getParent());

      Files.copy(file.getInputStream(), path);

      return new File(path.toString());

    } catch (IOException e) {
      LOG.error("could not save file", e);
      throw new FileException("could not create file: " + getPath(filePath), e);
    }
  }

  public File saveFile(String filePath, File file) {
    try {
      Path path = Paths.get(getPath(filePath));
      Files.createDirectories(path.getParent());

      Files.copy(new FileInputStream(file), path);
      return new File(path.toString());

    } catch (IOException e) {
      throw new FileException("could not create file: " + getPath(filePath), e);
    }
  }

  public void saveJPG(File image, String contentType, int newWidth, String outputPath) {
    try (InputStream imageInputStream = new FileInputStream(image)) {
      saveJPG(imageInputStream, image, contentType, newWidth, outputPath);
    } catch (Exception e) {
      LOG.error("could not create image stream", e);
    }
  }

  public void saveJPG(MultipartFile image, int newWidth, String outputPath) {
    try (InputStream imageInputStream = image.getInputStream()) {
      String contentType = image.getContentType();

      if (!Arrays.asList("image/png", "image/jpeg").contains(contentType)) {
        throw new IllegalArgumentException("The file provided is not a valid image or is not supported (should be png or jpeg): " + contentType);
      }

      // Create input image
      BufferedImage inputImage = ImageIO.read(imageInputStream);
      newWidth = newWidth > inputImage.getWidth() ? inputImage.getWidth() : newWidth;
      double ratio = (double) inputImage.getWidth() / (double) inputImage.getHeight();
      int scaledHeight = (int) (newWidth / ratio);

      // Create output image
      BufferedImage outputImage = new BufferedImage(newWidth,
        scaledHeight, BufferedImage.TYPE_INT_RGB);

      // Scale output
      Graphics2D g2d = outputImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.drawImage(inputImage, 0, 0, newWidth, scaledHeight, null);
      g2d.dispose();

      // Write output to file
      Path path = Paths.get(baseUrl + outputPath + ".jpg");
      Files.createDirectories(path);
      LOG.debug("writing image to {}", path);
      ImageIO.write(outputImage, "JPG", path.toFile());


    } catch (Exception e) {
      LOG.error("could not create image stream", e);
    }
  }

  /**
   * Method to resize image and save to jpg
   * extension set by the method
   *
   * @param contentType      type of image
   * @param imageInputStream image
   * @param newWidth         width desired
   * @param outputPath       image's path
   */
  private void saveJPG(InputStream imageInputStream, File file, String contentType, int newWidth, String outputPath) {
    try {
      // verify it is an image
      if (!Arrays.asList("image/png", "image/jpeg").contains(contentType)) {
        throw new IllegalArgumentException("The file provided is not a valid image or is not supported (should be png or jpeg): " + contentType);
      }

      // Create input image
      BufferedImage inputImage = ImageIO.read(imageInputStream);
      newWidth = newWidth > inputImage.getWidth() ? inputImage.getWidth() : newWidth;
      double ratio = (double) inputImage.getWidth() / (double) inputImage.getHeight();
      int scaledHeight = (int) (newWidth / ratio);

      Path path = Paths.get(baseUrl + outputPath + ".jpg");

      Thumbnails.of(file)
        .size(newWidth, scaledHeight)
        .toFile(path.toFile());

      LOG.debug("writing image to {}", path);

    } catch (IOException e) {
      LOG.error("could not write image", e);
    }
  }

  public void compressVideo(Path file, String videoPath) throws IOException {
    String inputPath = file.toString();
    LOG.info("writing file {} to {}", inputPath, getPath(videoPath));
    String videoCodec = "h264";
    String audioCodec = "aac";
    Process p = Runtime.getRuntime().exec(
      String.format(
        "ffmpeg -i %s -vf scale=-1:720 -vcodec %s -acodec %s %s",
        inputPath,
        videoCodec,
        audioCodec,
        getPath(videoPath)
      )
    );

    try {
      int exitCode = p.waitFor();
      if (exitCode != 0) {
        LOG.error("an error occured during compression");
        throw new IOException("an ffmpeg error occured: exit code " + exitCode);
      }
      LOG.info("compression ended");
    } catch (InterruptedException e) {
      LOG.error("could not complete video compression for file {}", videoPath, e);
      Thread.currentThread().interrupt();
    }
  }

  public void generateVideoThumbnail(Path file, String thumbnailPath) throws IOException {
    String inputPath = file.toString();
    LOG.info("writing file {} to {}", inputPath, getPath(thumbnailPath));
    Process p = Runtime.getRuntime().exec(
      String.format(
        "ffmpeg -i %s -ss 00:00:02 -vframes 1 -vf scale=-1:720 %s",
        inputPath,
        getPath(thumbnailPath)
      )
    );

    try {
      int exitCode = p.waitFor();
      if (exitCode != 0) {
        LOG.error("an error occured during thumbnail generation");
        throw new IOException("an ffmpeg error occured: exit code " + exitCode);
      }
    } catch (InterruptedException e) {
      LOG.error("could not complete thumbnail generation for file {}", thumbnailPath, e);
      Thread.currentThread().interrupt();
    }
  }


  public String getBaseUrl() {
    return baseUrl;
  }

  private String sanitizePath(String name) {
    return name
      .replaceAll("[^a-zA-Z0-9\\s]", "")
      .replaceAll(" ", "-");
  }

  public String getPath(String url) {
    return baseUrl + url;
  }

}
