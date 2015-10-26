package com.pho;

import java.util.ArrayList;
import java.util.List;

/**
 * A photo object.
 */
public class Photo {
    private static final String INITIAL_PHOTO_TITLE = "Untitled";

    private String photoId;
    private String title;
    private List<Version> versions;
    private List<Comment> comments;

    /**
     * Creates a new photo associated with a unique pId.
     * @param photoId string, a unique pId given by the server at creation time.
     */
    public Photo(String photoId) {
        // TODO: Extended Feature: initialize with given title.
        this.title = INITIAL_PHOTO_TITLE;
        this.photoId = photoId;

        this.comments = new ArrayList<>();
        // TODO: Initialize versions
    }

    /**
     * Retrieves the pId for this photo.
     * @return string, the unique pId of this photo.
     */
    public String getPhotoId() {
        return this.photoId;
    }

    /**
     * Retrieves the title of this photo.
     * @return string, the title of the photo.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Retrieves all the versions of this photo.
     * @return list of versions
     */
    public List<Version> getVersions() {
        return this.versions;
    }

    /**
     * Adds a newest version to the versions list of this photo.
     * @param version Version, a new version of this photo.
     */
    public void addVersion(Version version) {
	this.versions.add(version);
    }

    /**
     * Adds a new comment to the photo.
     * @param comment Comment, a new comment just created.
     */
    public void addComment(Comment comment) {
	this.comments.add(comment);
    }

    /**
     * Change the photo title.
     * @param newTitle string, the new desired title of this photo.
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
}
