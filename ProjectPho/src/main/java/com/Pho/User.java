package com.pho;

import java.util.List;

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
        // TODO: implement
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
}
