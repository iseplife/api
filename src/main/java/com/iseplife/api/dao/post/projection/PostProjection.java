package com.iseplife.api.dao.post.projection;


import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.entity.post.embed.Embedable;

public interface PostProjection {
    @Value("#{target.post.id}")
    Long getId();
    @Value("#{target.post.feed}")
    PostContextProjection getContext();
    @Value("#{target.post.publicationDate}")
    Date getPublicationDate();
    @Value("#{target.post.description}")
    String getDescription();
    @Value("#{target.post.pinned}")
    boolean isPinned();
    @Value("#{target.post.homepagePinned}")
    boolean isHomepagePinned();
    @Value("#{target.post.homepageForced}")
    boolean isHomepageForced();
    
    @Value("#{target.post.oldLikes}")
    Integer getOldLikes();

    @Value("#{target.post.embed}")
    Embedable getEmbed();

    @Value("#{target.post.linkedClub == null ? target.post.author: target.post.linkedClub}")
    AuthorProjection getAuthor();
    Long getThread();
    Integer getNbLikes();
    Integer getNbComments();
    boolean isLiked();
}
