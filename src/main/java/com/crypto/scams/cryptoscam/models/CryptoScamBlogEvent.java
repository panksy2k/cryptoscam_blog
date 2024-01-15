package com.crypto.scams.cryptoscam.models;

import java.util.List;

public final class CryptoScamBlogEvent {

  private final long id;
  private final String title;
  private final String description;
  private final String reference;
  private final Boolean blogActive;
  private final List<String> tags;


  public CryptoScamBlogEvent(long id, String title, String description, String reference, Boolean blogActive,
                             List<String> tags) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.reference = reference;
    this.blogActive = blogActive;
    this.tags = tags;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getReference() {
    return reference;
  }

  public Boolean getBlogActive() {
    return blogActive;
  }

  public List<String> getTags() {
    return tags;
  }

  public static CryptoScamBlogEvent createPersistedRecord(Long eventId, CryptoScamBlogEvent transientBlogEvent) {
    return new CryptoScamBlogEvent(eventId, transientBlogEvent.getTitle(), transientBlogEvent.getDescription(),
      transientBlogEvent.getReference(), transientBlogEvent.getBlogActive(), transientBlogEvent.getTags());
  }
}
