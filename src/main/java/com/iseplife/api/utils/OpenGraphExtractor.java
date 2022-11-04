package com.iseplife.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.opengraph4j.model.OpenGraphAudio;
import com.sigpwned.opengraph4j.model.OpenGraphImage;
import com.sigpwned.opengraph4j.model.OpenGraphMetadata;
import com.sigpwned.opengraph4j.model.OpenGraphVideo;

public class OpenGraphExtractor {
  private static final Logger LOGGER = LoggerFactory.getLogger(OpenGraphExtractor.class);

  private OpenGraphVideo.Builder videoBuilder;
  private OpenGraphAudio.Builder audioBuilder;
  private OpenGraphImage.Builder imageBuilder;
  private OpenGraphMetadata.Builder builder;

  public Optional<OpenGraphMetadata> extract(Document doc) {
    Elements metas = doc.select("meta[property][content]");
    System.out.println(metas.size()+" "+metas.select("[property=og:type]"));
    String type =
        Optional.ofNullable(metas.select("[property=og:type]").first()).map(e -> e.attr("content"))
            .map(String::strip).filter(Predicate.not(String::isEmpty)).orElse(null);

    OpenGraphMetadata result;
    if (type != null) {
      builder = OpenGraphMetadata.builder().withType(type);

      Iterator<Element> iterator = metas.iterator();
      while (iterator.hasNext()) {
        Element element = iterator.next();

        String property = element.attr("property").toLowerCase();
        String content = element.attr("content");

        metadata(property, content);
      }

      if (imageBuilder != null)
        builder.getImages().add(imageBuilder.build());

      if (videoBuilder != null)
        builder.getVideos().add(videoBuilder.build());

      if (audioBuilder != null)
        builder.getAudios().add(audioBuilder.build());

      result = builder.build();

      imageBuilder = null;
      videoBuilder = null;
      audioBuilder = null;
      builder = null;
    } else {
      result = null;
    }

    return Optional.ofNullable(result);
  }

  public static final String OG_TITLE_PROPERTY_NAME = "og:title";

  public static final String OG_TYPE_PROPERTY_NAME = "og:type";

  public static final String OG_URL_PROPERTY_NAME = "og:url";

  public static final String OG_DESCRIPTION_PROPERTY_NAME = "og:description";

  public static final String OG_DETERMINER_PROPERTY_NAME = "og:determiner";

  public static final String OG_LOCALE_PROPERTY_NAME = "og:locale";

  public static final String OG_LOCALE_ALTERNATE_PROPERTY_NAME = "og:locale:alternate";

  public static final String OG_SITE_NAME_PROPERTY_NAME = "og:site_name";

  public static final String OG_VIDEO_PROPERTY_NAME = "og:video";

  public static final String OG_VIDEO_SECURE_URL_PROPERTY_NAME = "og:video:secure_url";

  public static final String OG_VIDEO_TYPE_PROPERTY_NAME = "og:video:type";

  public static final String OG_VIDEO_WIDTH_PROPERTY_NAME = "og:video:width";

  public static final String OG_VIDEO_HEIGHT_PROPERTY_NAME = "og:video:height";

  public static final String OG_VIDEO_ALT_PROPERTY_NAME = "og:video:alt";

  public static final String OG_AUDIO_PROPERTY_NAME = "og:audio";

  public static final String OG_AUDIO_SECURE_URL_PROPERTY_NAME = "og:audio:secure_url";

  public static final String OG_AUDIO_TYPE_PROPERTY_NAME = "og:audio:type";

  public static final String OG_IMAGE_PROPERTY_NAME = "og:image";

  public static final String OG_IMAGE_SECURE_URL_PROPERTY_NAME = "og:image:secure_url";

  public static final String OG_IMAGE_TYPE_PROPERTY_NAME = "og:image:type";

  public static final String OG_IMAGE_WIDTH_PROPERTY_NAME = "og:image:width";

  public static final String OG_IMAGE_HEIGHT_PROPERTY_NAME = "og:image:height";

  public static final String OG_IMAGE_ALT_PROPERTY_NAME = "og:image:alt";

  public static final String ARTICLE_PUBLISHED_TIME_PROPERTY_NAME = "article:published_time";

  public static final String ARTICLE_MODIFIED_TIME_PROPERTY_NAME = "article:modified_time";

  public static final String ARTICLE_EXPIRATION_TIME_PROPERTY_NAME = "article:expiration_time";

  public static final String ARTICLE_AUTHOR_PROPERTY_NAME = "article:author";

  public static final String ARTICLE_SECTION_PROPERTY_NAME = "article:section";

  public static final String ARTICLE_TAG_PROPERTY_NAME = "article:tag";

  public static final String BOOK_AUTHOR_PROPERTY_NAME = "book:author";

  public static final String BOOK_ISBN_PROPERTY_NAME = "book:isbn";

  public static final String BOOK_RELEASE_DATE_PROPERTY_NAME = "book:release_date";

  public static final String BOOK_TAG_PROPERTY_NAME = "book:tag";

  public static final String PROFILE_FIRST_NAME_PROPERTY_NAME = "profile:first_name";

  public static final String PROFILE_LAST_NAME_PROPERTY_NAME = "profile:last_name";

  public static final String PROFILE_USERNAME_PROPERTY_NAME = "profile:username";

  public static final String PROFILE_GENDER_PROPERTY_NAME = "profile:gender";

  private void metadata(String property, String content) {
    switch (property) {
      case OG_TYPE_PROPERTY_NAME:
        // Skip. We've already handled it above.
        break;
      case OG_TITLE_PROPERTY_NAME:
        builder.setTitle(content);
        break;
      case OG_URL_PROPERTY_NAME:
        builder.setUrl(content);
        break;
      case OG_DESCRIPTION_PROPERTY_NAME:
        builder.setDescription(content);
        break;
      case OG_DETERMINER_PROPERTY_NAME:
        builder.setDeterminer(content);
        break;
      case OG_LOCALE_PROPERTY_NAME:
        builder.setLocale(content);
        break;
      case OG_LOCALE_ALTERNATE_PROPERTY_NAME:
        builder.getAlternateLocales().add(content);
        break;
      case OG_SITE_NAME_PROPERTY_NAME:
        builder.setSiteName(content);
        break;
      case OG_IMAGE_PROPERTY_NAME:
        if (imageBuilder != null)
          builder.getImages().add(imageBuilder.build());
        imageBuilder = OpenGraphImage.builder(content);
        break;
      case OG_IMAGE_SECURE_URL_PROPERTY_NAME:
        if (imageBuilder != null) {
          imageBuilder.setSecureUrl(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no image is currently in flight", property);
        }
        break;
      case OG_IMAGE_TYPE_PROPERTY_NAME:
        if (imageBuilder != null) {
          imageBuilder.setType(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no image is currently in flight", property);
        }
        break;
      case OG_IMAGE_WIDTH_PROPERTY_NAME:
        if (imageBuilder != null) {
          Integer width;
          try {
            width = new BigDecimal(content).setScale(0, RoundingMode.DOWN).intValueExact();
          } catch (NumberFormatException | ArithmeticException e) {
            LOGGER.debug("Ignoring image {} tag {} due to invalid value {}", imageBuilder.getUrl(),
                property, content);
            width = null;
          }
          imageBuilder.setWidth(width);
        } else {
          LOGGER.debug("Ignoring tag {} because no image is currently in flight", property);
        }
        break;
      case OG_IMAGE_HEIGHT_PROPERTY_NAME:
        if (imageBuilder != null) {
          Integer height;
          try {
            height = new BigDecimal(content).setScale(0, RoundingMode.DOWN).intValueExact();
          } catch (NumberFormatException | ArithmeticException e) {
            LOGGER.debug("Ignoring image {} tag {} due to invalid value {}", imageBuilder.getUrl(),
                property, content);
            height = null;
          }
          imageBuilder.setHeight(height);
        } else {
          LOGGER.debug("Ignoring tag {} because no image is currently in flight", property);
        }
        break;
      case OG_IMAGE_ALT_PROPERTY_NAME:
        if (imageBuilder != null) {
          imageBuilder.setAlt(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no image is currently in flight", property);
        }
        break;
      case OG_VIDEO_PROPERTY_NAME:
        if (videoBuilder != null)
          builder.getVideos().add(videoBuilder.build());
        videoBuilder = OpenGraphVideo.builder(content);
        break;
      case OG_VIDEO_SECURE_URL_PROPERTY_NAME:
        if (videoBuilder != null) {
          videoBuilder.setSecureUrl(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no video is currently in flight", property);
        }
        break;
      case OG_VIDEO_TYPE_PROPERTY_NAME:
        if (videoBuilder != null) {
          videoBuilder.setType(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no video is currently in flight", property);
        }
        break;
      case OG_VIDEO_WIDTH_PROPERTY_NAME:
        if (videoBuilder != null) {
          Integer width;
          try {
            width = new BigDecimal(content).setScale(0, RoundingMode.DOWN).intValueExact();
          } catch (NumberFormatException | ArithmeticException e) {
            LOGGER.debug("Ignoring video {} tag {} due to invalid value {}", videoBuilder.getUrl(),
                property, content);
            width = null;
          }
          videoBuilder.setWidth(width);
        } else {
          LOGGER.debug("Ignoring tag {} because no video is currently in flight", property);
        }
        break;
      case OG_VIDEO_HEIGHT_PROPERTY_NAME:
        if (videoBuilder != null) {
          Integer height;
          try {
            height = new BigDecimal(content).setScale(0, RoundingMode.DOWN).intValueExact();
          } catch (NumberFormatException | ArithmeticException e) {
            LOGGER.debug("Ignoring video {} tag {} due to invalid value {}", videoBuilder.getUrl(),
                property, content);
            height = null;
          }
          videoBuilder.setHeight(height);
        } else {
          LOGGER.debug("Ignoring tag {} because no video is currently in flight", property);
        }
        break;
      case OG_VIDEO_ALT_PROPERTY_NAME:
        if (videoBuilder != null) {
          videoBuilder.setAlt(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no video is currently in flight", property);
        }
        break;
      case OG_AUDIO_PROPERTY_NAME:
        if (audioBuilder != null)
          builder.getAudios().add(audioBuilder.build());
        audioBuilder = OpenGraphAudio.builder(content);
        break;
      case OG_AUDIO_SECURE_URL_PROPERTY_NAME:
        if (audioBuilder != null) {
          audioBuilder.setSecureUrl(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no audio is currently in flight", property);
        }
        break;
      case OG_AUDIO_TYPE_PROPERTY_NAME:
        if (audioBuilder != null) {
          audioBuilder.setType(content);
        } else {
          LOGGER.debug("Ignoring tag {} because no audio is currently in flight", property);
        }
        break;
      case ARTICLE_PUBLISHED_TIME_PROPERTY_NAME:
        try {
          builder.setArticlePublishedTime(OffsetDateTime.parse(content));
        } catch (DateTimeParseException e) {
          LOGGER.debug("Ignoring tag {} due to invalid value {}", property, content, e);
        }
        break;
      case ARTICLE_MODIFIED_TIME_PROPERTY_NAME:
        try {
          builder.setArticleModifiedTime(OffsetDateTime.parse(content));
        } catch (DateTimeParseException e) {
          LOGGER.debug("Ignoring tag {} due to invalid value {}", property, content, e);
        }
        break;
      case ARTICLE_EXPIRATION_TIME_PROPERTY_NAME:
        try {
          builder.setArticleExpirationTime(OffsetDateTime.parse(content));
        } catch (DateTimeParseException e) {
          LOGGER.debug("Ignoring tag {} due to invalid value {}", property, content, e);
        }
        break;
      case ARTICLE_AUTHOR_PROPERTY_NAME:
        builder.getArticleAuthors().add(content);
        break;
      case ARTICLE_SECTION_PROPERTY_NAME:
        builder.setArticleSection(content);
        break;
      case ARTICLE_TAG_PROPERTY_NAME:
        builder.getArticleTags().add(content);
        break;
      case BOOK_AUTHOR_PROPERTY_NAME:
        builder.getBookAuthors().add(content);
        break;
      case BOOK_ISBN_PROPERTY_NAME:
        builder.setBookIsbn(content);
        break;
      case BOOK_RELEASE_DATE_PROPERTY_NAME:
        try {
          builder.setBookReleaseDate(OffsetDateTime.parse(content));
        } catch (DateTimeParseException e) {
          LOGGER.debug("Ignoring tag {} due to invalid value {}", property, content, e);
        }
        break;
      case BOOK_TAG_PROPERTY_NAME:
        builder.getBookTags().add(content);
        break;
      case PROFILE_FIRST_NAME_PROPERTY_NAME:
        builder.setProfileFirstName(content);
        break;
      case PROFILE_LAST_NAME_PROPERTY_NAME:
        builder.setProfileLastName(content);
        break;
      case PROFILE_USERNAME_PROPERTY_NAME:
        builder.setProfileUsername(content);
        break;
      case PROFILE_GENDER_PROPERTY_NAME:
        builder.setProfileGender(content);
        break;
      default:
        LOGGER.trace("Ignoring tag {} because it is unrecognized", property);
        break;
    }
  }
}
