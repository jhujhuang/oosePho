package com.Pho;

import java.util.List;

/**
 * The Use class is for holding information of user.
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
