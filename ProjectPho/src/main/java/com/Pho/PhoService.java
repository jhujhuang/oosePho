package com.Pho;

import org.sql2o.Sql2o;
import javax.sql.DataSource;

import java.util.List;
import java.util.Map;


/**
 * Pho service.
 */
public class PhoService {

    private Sql2o db;

    /**
     * Constructor
     * @param dataSource the data source
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
     */
    public void register(String userId, String password) throws PhoServiceException {
        // TODO: Implement
    }

    /**
     * Log in to account
     * @param userId the user ID
     * @param password the user's password
     * @return the token
     */
    public String login(String userId, String password) throws PhoServiceException {
        return null;  // TODO: Implement
    }

    /**
     * Create a new photo
     * @param userId the user ID
     * @return the photo ID
     */
    public String createNewPhoto(String userId) throws PhoServiceException {
        return null;  // TODO: Implement
    }

    /**
     * Join editing session
     * @param userId the user ID
     * @param photoId the photo ID
     */
    public void joinEditingSession(String userId, String photoId) throws PhoServiceException {
        // TODO: Implement
    }

    /**
     * List photos of current user
     * @param userId the user ID
     */   
    public Map<String, List<String>> listPhotosOfCurrentUser(String userId) throws PhoServiceException {
        return null;  // TODO: Implement
    }

    /**
     * Edit photo title
     * @param title the title of the photo
     * @param photoId the photo ID
     */
    public void editPhotoTitle(String photoId, String title) {
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
     */
    public void edit(String userId, String photoId, String canvasId, String editType,
        Map<String, String> params) {
        // TODO: Implement
    }

    /**
     * Handles the fetch of current contents about a photo's editing session.
     *
     * @param photoId the photo ID
     * @return map of content to be included in the response to fetch
     */
    public Map<String, String> fetch(String photoId) {
        return null;  // TODO: Implement
    }

    /**
     * Comment on a photo.
     *
     * @param userId the user ID
     * @param photoId the photo ID
     * @param content comment content
     */
    public void comment(String userId, String photoId, String content) {
        // TODO: Implement
    }

    /**
     * Get all revisions
     * @param photoId the photo ID
     * @return map of content to be included in the response, where the list is a list of versions' information.
     */
    public Map<String, List<Version>> getRevisions(String photoId) {
        return null;  // TODO: Implement
    }

    /**
     * Revert to selected version
     * @param photoId the photo ID
     * @param versionId the version ID
     */
    public void revertToSelectedVersion(String photoId, String versionId) {
        // TODO: Implement
    }

    /**
     * Save the current version
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     */
    public void saveVersion(String userId, String photoId, String canvasId) {
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
}
