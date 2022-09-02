package com.iseplife.api.dao.post.projection;


import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.entity.post.embed.Embedable;

public interface PostSimpleProjection {
    @Value("#{target.id}")
    Long getId();
    @Value("#{target.feed}")
    PostContextProjection getContext();
    @Value("#{target.publicationDate}")
    Date getPublicationDate();
    @Value("#{target.description}")
    String getDescription();
    @Value("#{target.pinned}")
    boolean isPinned();
    @Value("#{target.homepagePinned}")
    boolean isHomepagePinned();
    @Value("#{target.homepageForced}")
    boolean isHomepageForced();

    @Value("#{target.embed}")
    Embedable getEmbed();

    @Value("#{target.linkedClub == null ? target.author: target.linkedClub}")
    AuthorProjection getAuthor();
}
