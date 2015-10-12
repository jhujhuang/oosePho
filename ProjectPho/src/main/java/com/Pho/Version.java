package com.Pho;

import java.awt.image.BufferedImage;

/**
 * Version class contains the actual picture of photo.
 * A Photo class contains a list of versions.
 */
public class Version {
    private String versionId, createdTime;
    private BufferedImage img;
    
    /**
     * Constructor for the Version class.
     * @param versionId the id of this version
     * @param createdTime the createdTime of this version
     * @param img the image of this version
     */
    public Version(String versionId, String createdTime, BufferedImage img) {
	this.versionId = versionId;
	this.createdTime = createdTime;
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
     * Get the image of this version.
     * @return the image of this verison
     */
    public BufferedImage getImage() {
	return this.img;
    }
}
