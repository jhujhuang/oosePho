package com.Pho;

import java.util.List;

/**
 * The Use class is for holding information of user.
 */
public class User {
    private String userName, userEmail;
    private List<Photo> photos;

    /**
     * Constructor for the User class
     * @param userName user name of the user
     * @param userEmail email of the user
     */
    public User(String userName, String userEmail) {
        // TODO: implement
    }

    /**
     * Get the user name of user.
     * @return the user name of the user
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Get the user email of this user.
     * @return the email of the user
     */
    public String getUserEmail() {
	return this.userEmail;
    }

    /** Get the list of photos of the user.
     * @return the array containing the photos of the user
     */
    public List<Photo> getPhotos() {
	return this.photos;
    }
}
