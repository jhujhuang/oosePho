package com.pho;

import java.util.List;
import java.util.ArrayList;

/**
 * pho user.
 * Each user is maintained with a unique userId and a list of owned photos.
 */
public class User {
    private String userId;
    private List<Photo> photos;

    /**
     * Constructor for the User class
     * @param userName string of the userId
     */
    public User(String userName) {
        this.userId = userName;
    }

    /**
     * Get the user ID of user.
     * @return the userId of the user
     */
    public String getUserId() {
        return this.userId;
    }

    /** Get the list of photos of the user.
     * @return the array containing the photos of the user
     */
    public List<Photo> getPhotos() {
	return this.photos;
    }
    
    /** Add a photo to a user's collection of photo.
     * @param p the photo to add
     */
    public void addPhoto(Photo p) {
	// TODO: implement
    }
}
