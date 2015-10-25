package com.pho;

import org.sql2o.Sql2o;
import javax.sql.DataSource;

import java.util.List;
import java.util.Map;


/**
 * pho service. The model ("M") of the MVC model.
 */
public class PhoService {

    private Sql2o db;
    private List<User> users;
    private List<EditingSession> editingSessions;

    /**
     * Constructor
     * @param dataSource the data source
     * @throws PhoServiceException when failures occur
     */
    public PhoService(DataSource dataSource) throws PhoServiceException {
        // create a new database
        db = new Sql2o(dataSource);

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
     * Authenticate the user
     * @param userId the unique user ID
     * @param token the generated token for specific user
     * @return whether the user has been authenticated
     */
    public boolean authenticate(String userId, String token) {
        return true; // TODO: Implement
    }

    /**
     * Register new account
     * @param userId the user ID
     * @param password the user's password
     * @throws PhoServiceException when failures occur
     */
    public void register(String userId, String password) throws PhoServiceException {
        // TODO: Implement
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
    public String createNewPhoto(String userId) throws InvalidTokenException {
        return null;  // TODO: Implement
    }

    /**
     * Join editing session
     * @param userId the user ID
     * @param photoId the photo ID
     * @throws PhoServiceException when failures occur
     */
    public void joinEditingSession(String userId, String photoId) throws InvalidTokenException, InvalidPhotoIdException {
        // TODO: Implement
    }

    /**
     * List photos of current user
     * @param userId the user ID
     * @throws PhoServiceException when failures occur
     * @return map of content to be included in the response, where the list is a list of userId's
     */   
    public Map<String, List<String>> listPhotosOfCurrentUser(String userId) throws InvalidTokenException {
        return null;  // TODO: Implement
    }

    /**
     * Edit photo title
     * @param title the title of the photo
     * @param photoId the photo ID
     */
    public void editPhotoTitle(String photoId, String title) throws InvalidTokenException, InvalidPhotoIdException {
        // TODO: Implement
    }

    /**
     * Make an edit to a photo.
     *
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     * @param editType the edit type
     * @param params other parameters
     * @return newCanvasId the new canvas ID
     */
<<<<<<< HEAD
    public String edit(String userId, String photoId, String canvasId, String editType,
        Map<String, String> params) {
=======
    public void edit(String userId, String photoId, String canvasId, String editType, Map<String, String> params)
            throws InvalidTokenException, InvalidPhotoIdException, PhoSyncException, PhoServiceException {
>>>>>>> c063dc0eb9b7d176b94feda557e4c1fbe9ccf69e
        // TODO: Implement
        return "new canvas ID";
    }

    /**
     * Handles the fetch of current contents about a photo's editing session.
     *
     * @param photoId the photo ID
     * @return map of content to be included in the response to fetch
     */
    public Map<String, String> fetch(String photoId) throws InvalidTokenException, InvalidPhotoIdException {
        return null;  // TODO: Implement
    }

    /**
     * Comment on a photo.
     *
     * @param userId the user ID
     * @param photoId the photo ID
     * @param content comment content
     */
    public void comment(String userId, String photoId, String content)
            throws InvalidTokenException, InvalidPhotoIdException {
        // TODO: Implement
    }

    /**
     * Get all revisions
     * @param photoId the photo ID
     * @return map of content to be included in the response, where the list is a list of Version instances.
     */
    public Map<String, List<Version>> getRevisions(String photoId)
            throws InvalidTokenException, InvalidPhotoIdException {
        return null;  // TODO: Implement
    }

    /**
     * Revert to selected version
     * @param photoId the photo ID
     * @param versionId the version ID
     */
    public void revertToSelectedVersion(String photoId, String versionId)
            throws InvalidTokenException, InvalidPhotoIdException, PhoServiceException {
        // TODO: Implement
    }

    /**
     * Save the current version
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     */
    public void saveVersion(String userId, String photoId, String canvasId)
            throws InvalidTokenException, InvalidPhotoIdException, PhoSyncException {
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
