package com.pho;

import org.hashids.Hashids;
import org.sql2o.Sql2o;
import javax.sql.DataSource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;


/**
 * pho service. The model ("M") of the MVC model.
 */
public class PhoService {
    // TODO: Confirm hash salt before using.
    private static final String HASH_SALT = "AlWaYs UnIqUe NeVeR ChAnGe";

    private Sql2o db;
    private List<User> users;
    // All photos in allPhotos list are stored in an order
    private List<Photo> allPhotos;
    // Since we store all photos in a List, cannot scale up to long now
    private int pIdTracker;

    /**
     * Constructor
     * @param dataSource the data source
     * @throws PhoServiceException when failures occur
     */
    public PhoService(DataSource dataSource) throws PhoServiceException {
        // Initializes users & allPhotos lists
        users = new ArrayList<>();
        allPhotos = new ArrayList<>();

        // TODO: track pId with persistence layer
        pIdTracker = 0;

        // create a new database
        db = new Sql2o(dataSource);

        // TODO: Load users from database & load allPhotos from database

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
     * Create a new photo
     * @param userId the user ID
     * @param image BufferedImage with image content
     * @throws PhoServiceException when failures occur
     * @return the photo ID
     */
    public String createNewPhoto(String userId, BufferedImage image) throws PhoServiceException {
        User usr = getUser(userId);

        String pId = getStringId(pIdTracker);

        // Add new photo associated with the given image.

        String time = getTime();
        Photo p = new Photo(pId, time, userId, image);
        allPhotos.add(pIdTracker, p);
        pIdTracker++;

        usr.addPhoto(p);  // User is authenticated at this point.

        return pId;
    }

    /**
     * Join editing session of a Photo
     * @param userId the user ID
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when failures occur
     */
    public void joinEditingSession(String userId, String photoId) throws InvalidPhotoIdException {
        Photo e = findByPhotoId(photoId);
        User usr = getUser(userId);
        e.addCollaborator(usr);
    }

    /**
     * List photos of current user
     * @param userId the user ID
     * @return map of content to be included in the response, where the list is a list of userId's
     */   
    public Map<String, Map<String, String>> listPhotosOfCurrentUser(String userId) {
        User usr = getUser(userId);
        Map<String, Map<String, String>> result = new HashMap<>();
        Map<String, String> l = new HashMap<>();
        for (Photo p: usr.getPhotos()) {
            String pId = p.getPId();
            try {
                l.put(pId, p.getImageBytes());
            } catch (IOException e1) {
                e1.printStackTrace();  // TODO: handle
            }
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
        Photo p = findByPhotoId(photoId);
        p.setTitle(title);
    }

    /**
     * Make an edit to a photo.
     *
     * @param photoId the photo ID
     * @param canvasId the canvas ID
     * @param editType the edit type
     * @param params other parameters
     * @throws InvalidPhotoIdException when photo id is invalid
     * @throws PhoSyncException if canvas in out of date
     * @throws PhoServiceException when failures occur
     * @return newCanvasId the new canvas ID
     */
    public void edit(String photoId, String canvasId, String editType, Map<String, Double> params,
                     Map<String, Double> select)
            throws InvalidPhotoIdException, PhoSyncException, PhoServiceException {
        Photo p = findByPhotoId(photoId);
        if (!canvasId.equals(p.getCanvasId())) {
            throw new PhoSyncException("Canvas is out of date.", null);
        }
        p.edit(editType, params, select);
    }

    /**
     * Get the current canvas id of a photo.
     * @param photoId the photo ID
     * @return canvasId
     * @throws InvalidPhotoIdException when photo id is invalid
     */
    public String getCanvasId(String photoId) throws InvalidPhotoIdException {
        Photo p = findByPhotoId(photoId);
        return p.getCanvasId();
    }

    /**
     * Handles the fetch of current contents about a photo.
     *
     * @param photoId the photo ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @return Photo.FetchResult content to be included in the response to fetch
     */
    public Photo.FetchResult fetch(String photoId) throws InvalidPhotoIdException {
        Photo p = findByPhotoId(photoId);
        try {
            return p.getFetchResults();
        } catch (IOException ex) {
            throw new RuntimeException("Something went wrong when we try to write image to bytes.");
        }
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
        Photo p = findByPhotoId(photoId);
        List<Version> versions = p.getVersions();
        Map<String, List<Version>> map = new HashMap<>();
        map.put("versions", versions);
        return map;
    }

    /**
     * Revert to selected version
     * @param photoId the photo ID
     * @param userId the user ID of the user who requests the revert
     * @param versionId the version ID
     * @throws InvalidPhotoIdException when photo id is invalid
     * @throws PhoServiceException when versionId is invalid
     */
    public void revertToSelectedVersion(String photoId, String versionId, String userId)
            throws InvalidPhotoIdException, PhoServiceException {
        Photo p = findByPhotoId(photoId);
        try {
            p.revertVersion(getTime(), versionId, userId);
        } catch (IndexOutOfBoundsException|NumberFormatException ex) {
            throw new PhoServiceException("Invalid versionId", ex);
        }
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
        Photo p = findByPhotoId(photoId);
        if (!canvasId.equals(p.getCanvasId())) {
            throw new PhoSyncException("Canvas is out of date.", null);
        }

        p.addVersion(getTime(), userId);
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    /**
     * Look for a user in the users list by userId.
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
     * Find a user in the users list by userId.
     * @param userId the userId String
     * @return user a User object
     * @throws IllegalArgumentException when user is not found
     */
    private User getUser(String userId) {
        User usr = findByUserId(userId);

        if (usr == null) {
            throw new IllegalArgumentException("Invalid user id");  // Should never happen
        }
        return usr;
    }

    /**
     * Find an a specific photo, by decoding the string pId.
     * @param pId the pId string
     * @return Photo
     * @throws InvalidPhotoIdException when cannot find by given pId
     * @throws IllegalArgumentException when pId is successfully decoded but photo record is not found
     */
    private Photo findByPhotoId(String pId) throws InvalidPhotoIdException {
        int index = getIntId(pId);
        Photo p = allPhotos.get(index);
        if (p == null) {  // Should never happen per current implementation
            throw new IllegalArgumentException("Cannot find valid photo by given pId.");
        }
        return p;
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
     * @throws InvalidPhotoIdException when string given is illegal
     * @return int
     */
    private int getIntId(String pId) throws InvalidPhotoIdException {
        Hashids hashids = new Hashids(HASH_SALT);
        long[] decode =  hashids.decode(pId);
        if (decode.length != 1) {  // Was encoded with one integer (pIdTracker)
            throw new InvalidPhotoIdException("Given string was not encoded with same hashids", null);
        }
        return (int) decode[0];
    }

    private String getTime() {
        Date d = new Date();
        return d.toString();
    }


    public static class PhoServiceException extends Exception {
        public PhoServiceException(String message, Throwable cause) {
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
