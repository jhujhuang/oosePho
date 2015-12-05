package com.pho;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A photo object.
 */
public class Photo {
    private static final String INITIAL_PHOTO_TITLE = "Untitled";

    private String photoId;
    private String title;
    private int nextVId;
    private List<Version> versions;
    // TODO: Maybe store and handle comments under EditingSession instead
    private List<Comment> comments;

    /**
     * Creates a new photo (empty with no version) associated with a unique pId.
     * @param photoId string, a unique pId given by the server at creation time.
     * @param time string
     * @param userId string
     * @param img BufferedImage
     */
    public Photo(String photoId, String time, String userId, BufferedImage img) {
        // TODO: Extended Feature: initialize with given title.
        this.title = INITIAL_PHOTO_TITLE;
        this.photoId = photoId;

        this.nextVId = 0;
        this.versions = new ArrayList<>();

        addVersion(time, userId, img);
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
     * Retrieves the latest version of this photo
     * @return Version
     */
    public Version getCurrentVersion() {
        return versions.get(versions.size() - 1);
    }

    /**
     * Adds a newest version to the versions list of this photo.
     * @param time string
     * @param userId string
     * @param img BufferedImage
     */
    public void addVersion(String time, String userId, BufferedImage img) {
        String vId = getNextVId();
        Version v = new Version(vId, time, userId, img);
        this.versions.add(v);
        nextVId++;
    }

    /**
     * Retrieves all the comments of this photo.
     * @return list of comments
     */
    public List<Comment> getComments() {
        return this.comments;
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

    /**
     * Return the next versionId string unique among versions for this photo
     * @return string
     */
    String getNextVId() {
        return "" + nextVId;  // TODO: Consider change how to make this versionId
    }
}
