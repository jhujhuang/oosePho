package com.pho;

import java.awt.image.BufferedImage;

/**
 * Version class contains the actual picture of photo.
 * A Photo class contains a list of versions.
 */
public class Version {
    private String versionId, createdTime, userId;
    private BufferedImage img;
    
    /**
     * Constructor for the Version class.
     * @param versionId the id of this version
     * @param createdTime the createdTime of this version
     * @param userId the userId of the user who saved this version.
     * @param img the image of this version
     */
    public Version(String versionId, String createdTime, String userId, BufferedImage img) {
    	this.versionId = versionId;
        this.createdTime = createdTime;
        this.userId = userId;
        this.img = img;
    }

    /**
     * Get the versionId of this version.
     * @return the version id of this version
     */ 
    public String getVersionId() {
        return this.versionId;
    }

    /**
     * Get the created time of this version.
     * @return the created time of this version
     */
    public String getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Retrieves the userId of the user who saved this version.
     * @return string, userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Get the image of this version.
     * @return the image of this version
     */
    public BufferedImage getImage() {
        return this.img;
    }
}
