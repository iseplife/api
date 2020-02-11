@AnyMetaDef(
  name = "postEmbed",
  metaType = "string",
  idType = "long",
  metaValues = {
    @MetaValue(value = EmbedType.GALLERY, targetEntity = Gallery.class),
    @MetaValue(value = EmbedType.POLL, targetEntity = Poll.class),
    @MetaValue(value = EmbedType.DOCUMENT, targetEntity = Document.class),
    @MetaValue(value = EmbedType.VIDEO, targetEntity = Video.class),
    @MetaValue(value = EmbedType.IMAGE, targetEntity = Image.class)
  }
)
package com.iseplife.api.entity.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.poll.Poll;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
