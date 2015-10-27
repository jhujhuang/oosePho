package com.pho;

import org.hashids.Hashids;
import org.sql2o.Sql2o;
import javax.sql.DataSource;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * pho service. The model ("M") of the MVC model.
 */
public class PhoService {
    // TODO: Consider to maintain users & editingSessions lists in an order for fast lookup.

    // TODO: Confirm hash salt before using.
    private static final String HASH_SALT = "AlWaYs UnIqUe NeVeR ChAnGe";

    private Sql2o db;
    private List<User> users;
    private List<EditingSession> editingSessions;
    // Since we store all editing sessions in a List, cannot scale up to long now
    private int pIdTracker;

    /**
     * Constructor
     * @param dataSource the data source
     * @throws PhoServiceException when failures occur
     */
    public PhoService(DataSource dataSource) throws PhoServiceException {
        // Initializes users & editingSessions lists
        users = new ArrayList<>();
        editingSessions = new ArrayList<>();

        // TODO: track pId with persistence layer
        pIdTracker = 0;

        // create a new database
        db = new Sql2o(dataSource);

        // TODO: Load users from database & load editingSessions from database

        // TODO: Implement
        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        /*try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new GameServiceException("Failed to create schema at startup", ex);
        }*/
    }

    /**
     * Find a user in the users list by userId.
     * @param userId the userId string
     * @return user a User object, or null if not found
     */
    private User findByUserId(String userId) {
        for (User usr: users) {
            if (usr.getUserId().equals(userId)) {
                return usr;
            }
        }
        return null;
    }

    /**
     * Find an editing session for a specific photo, by decoding the string pId.
     * @param pId the pId string
     * @return editing session
     * @throws InvalidPhotoIdException when cannot find by given pId
     */
    private EditingSession findByPhotoId(String pId) throws InvalidPhotoIdException {
        int index;
        try {
            index = getIntId(pId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPhotoIdException("Cannot find valid photo.", ex);
        }
        EditingSession e = editingSessions.get(index);
        if (e == null) {  // Should never happen per current implementation
            throw new InvalidPhotoIdException("Cannot find valid photo.", null);
        }
        return e;
    }

    /**
     * Generate unique non-guessable strings based on a unique number.
     * @param num int, a number the generated string id will correspond to
     * @return string
     */
    private String getStringId(int num) {
        Hashids hashids = new Hashids(HASH_SALT);
        return hashids.encode(num);
    }

    /**
     * Decode generated hashids strings back to integer.
     * @param pId string, a valid hashids string generated with HASH_SALT
     * @throws IllegalArgumentException when string given is illegal
     * @return int
     */
    private int getIntId(String pId) {
        Hashids hashids = new Hashids(HASH_SALT);
        long[] decode =  hashids.decode(pId);
        if (decode.length != 1) {  // Was encoded with an int pIdTracker at that time
            throw new IllegalArgumentException("Given string was not encoded with same hashids");
        }
        return (int) decode[0];
    }

    /**
     * Authenticate the user
     * @param userId the unique user ID
     * @param token the generated token for specific user
     * @throws InvalidTokenException when authentication fails
     * @return whether the user has been authenticated
     */
    public boolean authenticate(String userId, String token) throws InvalidTokenException {
        return true; // TODO: Implement
    }

    /**
     * Register new account
     * @param userId the user ID
     * @param password the user's password
     * @throws PhoServiceException when the userId already exists
     */
    public void register(String userId, String password) throws PhoServiceException {
        // Check if userId is existing yet
        if (findByUserId(userId) != null) {
            throw new PhoServiceException("Existing userId", null);
        }

        // Store user password
        // TODO: implement

        // Create new user
        User user = new User(userId);
        users.add(user);
    }

    /**
     * Log in to account
     * @param userId the user ID
     * @param password the user's password
     * @throws PhoServiceException when failures occur
     * @return the token
     */
    public String login(String userId, String password) throws PhoServiceException {
        return "dummy token";  // TODO: Implement
    }

    /**
     * Create a new photo
     * @param userId the user ID
     * @throws PhoServiceException when failures occur
     * @return the photo ID
     */
    public String createNewPhoto(String userId, BufferedImage image) throws PhoServiceException {
        User usr = findByUserId(userId);
        String pId = getStringId(pIdTracker);
        Photo p = new Photo(pId);

        // Add version based on given image
        String vId = p.getNextVId();
        String time = "0000-00"; // TODO: Get actual time
        Version v0 = new Version(vId, time, userId, image);
        p.addVersion(v0);

        usr.addPhoto(p);  // User is authenticated at this point.

        // Add editing session associated with the new photo.
        EditingSession e = new EditingSession(p);
        editingSessions.add(pIdTracker, e);
        pIdTracker++;

        return pId;
    }

    /**
     * Join editing session
     * @param userId the user ID
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when failures occur
     */
    public void joinEditingSession(String userId, String photoId) throws InvalidPhotoIdException {
        EditingSession e = findByPhotoId(photoId);
        User usr = findByUserId(userId);
        e.addCollaborator(usr);
    }

    /**
     * List photos of current user
     * @param userId the user ID
     * @return map of content to be included in the response, where the list is a list of userId's
     */   
    public Map<String, List<String>> listPhotosOfCurrentUser(String userId) {
        User usr = findByUserId(userId);
        Map<String, List<String>> result = new HashMap<>();
        List<String> l = new ArrayList<>();
        for (Photo p: usr.getPhotos()) {
            l.add(p.getPhotoId());
        }
        result.put("photos", l);
        return result;
    }

    /**
     * Edit photo title
     * @param title the title of the photo
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when photo id is invalid
     */
    public void editPhotoTitle(String photoId, String title) throws InvalidPhotoIdException {
        EditingSession e = findByPhotoId(photoId);
        Photo p = e.getPhoto();
        p.setTitle(title);
    }

    /**
     * Make an edit to a photo.
     *
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     * @param editType the edit type
     * @param params other parameters
     * @throws InvalidPhotoIdException when photo id is invalid
     * @throws PhoSyncException if canvas in out of date
     * @throws PhoServiceException when failures occur
     * @return newCanvasId the new canvas ID
     */
    public String edit(String userId, String photoId, String canvasId, String editType, Map<String, String> params)
            throws InvalidPhotoIdException, PhoSyncException, PhoServiceException {
        // TODO: Implement
        return "new canvas ID";
    }

    /**
     * Handles the fetch of current contents about a photo's editing session.
     *
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @return map of content to be included in the response to fetch
     */
    public Map<String, String> fetch(String photoId) throws InvalidPhotoIdException {
        return null;  // TODO: Implement
    }

    /**
     * Comment on a photo.
     *
     * @param userId the user ID
     * @param photoId the photo ID
     * @param content comment content
     * @throws InvalidPhotoIdException when photo id is invalid
     */
    public void comment(String userId, String photoId, String content) throws InvalidPhotoIdException {
        // TODO: Implement
    }

    /**
     * Get all revisions
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @return map of content to be included in the response, where the list is a list of Version instances.
     */
    public Map<String, List<Version>> getRevisions(String photoId) throws InvalidPhotoIdException {
        Photo p = findByPhotoId(photoId).getPhoto();
        List<Version> versions = p.getVersions();
        Map<String, List<Version>> map = new HashMap<>();
        map.put("versions", versions);
        return map;
    }

    /**
     * Revert to selected version
     * @param photoId the photo ID
     * @param versionId the version ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @throws PhoServiceException when failures occur
     */
    public void revertToSelectedVersion(String photoId, String versionId)
            throws InvalidPhotoIdException, PhoServiceException {
        // TODO: Implement
    }

    /**
     * Save the current version
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @throws PhoSyncException when canvas is out of date
     */
    public void saveVersion(String userId, String photoId, String canvasId)
            throws InvalidPhotoIdException, PhoSyncException {
        // TODO: Implement
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class PhoServiceException extends Exception {
        public PhoServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidTokenException extends Exception {
        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidPhotoIdException extends Exception {
        public InvalidPhotoIdException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class PhoSyncException extends Exception {
        public PhoSyncException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
