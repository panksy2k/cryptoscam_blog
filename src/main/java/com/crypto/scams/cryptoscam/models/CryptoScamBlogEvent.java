package com.crypto.scams.cryptoscam.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CryptoScamBlogEvent {

    private final long id;
    private final String title;
    private final String description;
    private final String reference;
    private final String blogName;
    private final Boolean blogActive;
    private final String[] tags;


    public CryptoScamBlogEvent() {
        this(0, null, null, null, false, null, Collections.emptyList());
    }

    public CryptoScamBlogEvent(long id, String title, String description, String reference, Boolean blogActive,
                               String blogName, List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reference = reference;
        this.blogActive = blogActive;
        this.blogName = blogName;
        this.tags = tags.toArray(String[]::new);
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

    public String[] getTags() {
        return tags;
    }

    public String getBlogName() {
        return blogName;
    }

    public static CryptoScamBlogEvent createPersistedRecord(Long eventId, CryptoScamBlogEvent transientBlogEvent) {
        return new CryptoScamBlogEvent(eventId, transientBlogEvent.getTitle(), transientBlogEvent.getDescription(),
                transientBlogEvent.getReference(), transientBlogEvent.getBlogActive(), transientBlogEvent.getBlogName(),
                Arrays.asList(transientBlogEvent.getTags()));
    }
}
