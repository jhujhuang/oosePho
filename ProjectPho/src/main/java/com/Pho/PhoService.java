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
     * @param user Id the user ID
     * @param token the generated token for specific user
     * @return whether the user has been authenticated
     */
    public boolean authenticate(String userId, String token) {

    }

    /**
     * Register new account
     * @param userId the user ID
     * @param password the user's password
     */
    public void register(String userId, String password) throws PhoServiceException {

    }

    /**
     * Log in to account
     * @param userId the user ID
     * @param password the user's password
     * @return the token
     */
    public String login(String userId, String password) throws PhoServiceException {

    }

    /**
     * Create a new photo
     * @param userId the user ID
     * @return the photo ID
     */
    public String createNewPhoto(String userId) throws PhoServiceException {

    }

    /**
     * Join editing session
     * @param userId the user ID
     * @param photoId the photo ID
     */
    public void joinEditingSession(String userId, String photoId) throws PhoServiceException {

    }

    /**
     * List photos of current user
     * @param userId the user ID
     */   
    public Map<String, List<String>> listPhotosOfCurrentUser(String userId) throws PhoServiceException {

    }

    /**
     * Edit photo title
     * @param title the title of the photo
     * @param photoId the photo ID
     */
    public void editPhotoTitle(String photoId, String title) {

    }

    /**
     * Edit
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     * @param editType the edit type
     * @param params other parameters
     */
    public void edit(String userId, String photoId, String canvasId, String editType,
        Map<String, String> params) {

    }

    /**
     * Register new account
     * @param photoId the photo ID
     * @return a map of canvas
     */
    public Map<String, String> fetchCanvas(String photoId) {

    }

    /**
     * leave comment
     * @param userId the user ID
     * @param photoId the photo ID
     * @param content comment content
     */
    public void comment(String userId, String photoId, String content) {

    }

    /**
     * Get all revisions
     * @param photoId the photo ID
     * @return map of different versions
     */
    public Map<String, List<String>> getRevisions(String photoId) {

    }

    /**
     * Revert to selected version
     * @param photoId the photo ID
     * @param versionId the version ID
     */
    public void revertToSelectedVersion(String photoId, String versionId) {

    }

    /**
     * Save the current version
     * @param userId the user ID
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     */
    public void saveVersion(String userId, String photoId, String canvasId) {

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
