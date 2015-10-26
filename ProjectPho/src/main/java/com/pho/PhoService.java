package com.pho;

import org.sql2o.Sql2o;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * pho service. The model ("M") of the MVC model.
 */
public class PhoService {
    // TODO: Consider to maintain users & editingSessions lists in an order for fast lookup.

    private Sql2o db;
    private List<User> users;
    private List<EditingSession> editingSessions;
    private Long pIdTracker;

    /**
     * Constructor
     * @param dataSource the data source
     * @throws PhoServiceException when failures occur
     */
    public PhoService(DataSource dataSource) throws PhoServiceException {
        // Initializes users & editingSessions lists
        users = new ArrayList<>();
        editingSessions = new ArrayList<>();
        pIdTracker = 0L;

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
     * Find an editing session for a specific photo.
     * @param pId the pId string
     * @return editing session, or null if not found.
     */
    private EditingSession findByPhotoId(String pId) {
        for (EditingSession e: editingSessions) {
            if (e.getPhoto().getPhotoId().equals(pId)) {
                return e;
            }
        }
        return null;
    }

    private String getStringId(Long l) {
        // TODO: Use org.hashids to generate non-guessable strings.
        return l.toString();
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
        return null;  // TODO: Implement
    }

    /**
     * Create a new photo
     * @param userId the user ID
     * @throws PhoServiceException when failures occur
     * @return the photo ID
     */
    public String createNewPhoto(String userId) throws PhoServiceException {
        User usr = findByUserId(userId);
        String pId = getStringId(pIdTracker++);
        Photo p = new Photo(pId);
        // TODO: Add version based given image
        usr.addPhoto(p);  // User is authenticated at this point.

        // Add editing session associated with the new photo.
        EditingSession e = new EditingSession(p);

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
        if (e == null) {
            throw new InvalidPhotoIdException("Cannot find photo.", null);
        }
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
        for (Photo p: usr.getPhotos()) {  // TODO: QUESTION: Should User store photoIds instead of Photos?
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
        return null;  // TODO: Implement
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
