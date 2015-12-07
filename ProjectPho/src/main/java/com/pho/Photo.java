package com.pho;

import com.pho.filters.Filter;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Editing session.
 */
public class Photo {
    private static final String INITIAL_PHOTO_TITLE = "Untitled";

    private final String photoId;
    private String title;

    // Current canvas image of this photo
    private BufferedImage canvas;

    // Variables to help manage synchronization
    private int canvasIdInt;
    private String canvasId;

    // Variables to help manage version control
    private int nextVId;
    private List<Version> versions;

    // Collaborators of the photo
    private List<User> collaborators;

    // Comments made to this photo through collaboratively editing panel
    private List<Comment> comments;


    /**
     * Constructor for the EditingSessions class, initialized with the first image version
     * @param photoId string, a unique pId given by the server at creation time.
     * @param time string
     * @param userId string
     * @param img BufferedImage
     */
    public Photo(String photoId, String time, String userId, BufferedImage img) {
        // TODO: Extended Feature: initialize with given title.
        this.title = INITIAL_PHOTO_TITLE;
        this.photoId = photoId;

        this.nextVId = 0;
        this.versions = new ArrayList<>();

        // Set up canvas and initialize with 0 collaborators
        this.canvas = img;
        collaborators = new ArrayList<>();

        // Add the initial version to the versions list
        addVersion(time, userId);

        // Initialize canvasId
        canvasIdInt = 0;
        updateCanvasId();
    }

    /**
     * Return the photoId of the photo
     * @return String
     */
    public String getPId() {
        return photoId;
    }

    //-----------------------------------------------------------------------------//
    // Methods for the old Photo class
    //-----------------------------------------------------------------------------//


    /**
     * Retrieves the title of this photo.
     * @return string, the title of the photo.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Retrieves all the versions of this photo.
     * @return list of versions
     */
    public List<Version> getVersions() {
        return this.versions;
    }

    /**
     * Retrieves the latest version of this photo
     * @return Version
     */
    public Version getCurrentVersion() {
        return versions.get(versions.size() - 1);
    }

    /**
     * Adds a newest version to the versions list of this photo.
     * @param time string
     * @param userId string
     */
    public void addVersion(String time, String userId) {
        String vId = getNextVId();
        Version v = new Version(vId, time, userId, canvas);
        this.versions.add(v);
    }


    /**
     * Revert to an old version, and adds a newest version that has same image.
     * @param time string
     * @param versionId string
     * @param userId string
     * @throws NumberFormatException when versionId cannot convert to number
     * @throws IndexOutOfBoundsException when versionId is not found
     */
    public void revertVersion(String time, String versionId, String userId) {
        int versionIndex = Integer.parseInt(versionId);  // We use same version id as list index
        Version v = versions.get(versionIndex);
        BufferedImage img = v.getImage();

        Version nV = new Version(getNextVId(), time, userId, img);
        versions.add(nV);

        // Update canvas
        canvas = img;
    }


    /**
     * Retrieves all the comments of this photo.
     * @return list of comments
     */
    public List<Comment> getComments() {
        return this.comments;
    }

    /**
     * Adds a new comment to the photo.
     * @param comment Comment, a new comment just created.
     */
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    /**
     * Change the photo title.
     * @param newTitle string, the new desired title of this photo.
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }


    //-----------------------------------------------------------------------------//
    // Methods for the old EditingSession class
    //-----------------------------------------------------------------------------//


    /**
     * Add a collaborator to an editing session. Does nothing if user is already in.
     * @param collaborator User to add to the editing session.
     */
    public void addCollaborator(User collaborator) {
        if (collaborators.contains(collaborator)) {
            return;
        }
        collaborators.add(collaborator);
    }

    /**
     * Get the list of all collaborators
     * @return a list of User
     */
    public List<User> getCollaborators() {
        return collaborators;
    }

    /**
     * Get the canvas Id
     * @return canvas Id
     */
    public String getCanvasId() {
        return canvasId;
    }

    /**
     * Apply filter to photo, and update canvas.
     * @param editType String of filter type.
     * @param params Map of filter parameters.
     * @throws PhoService.PhoServiceException when editType is not found.
     */
    public void edit(String editType, Map<String, Double> params)
            throws PhoService.PhoServiceException {
        Filter f;
        try {
            f = Filter.getFilter(editType, params);
        } catch (Filter.UnknownFilterException e) {
            throw new PhoService.PhoServiceException("Invalid editing type", e);
        }
        f.loadImage(canvas);

        // Make changes and store new canvas image
        // TODO: Should put x0, x1, y0, y1 in params, so can also apply to selection?
        f.applyToRectangle(0, canvas.getWidth(), 0, canvas.getHeight());

        canvas = f.getImage();

        updateCanvasId();
    }

    /**
     * Retrieves the result for fetch
     * @return FetchResult object
     * @throws IOException when fails to get image bytes
     */
    FetchResult getFetchResults() throws IOException {
        FetchResult result = new FetchResult();
        result.setCanvasId(canvasId);
        result.setCollaborators(collaborators);
        result.setTitle(title);
        result.setCanvasData(getImageBytes());
        // TODO: Do we need versionId fetched?
        Version currentVersion = getCurrentVersion();
        result.setVersionId(currentVersion);
        return result;
    }

    /**
     * Get the image in the form of base 64 bytes.
     * @return base 64 string
     * @throws IOException when fails to get image bytes
     */
    public String getImageBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "jpg", baos);
        baos.flush();
        byte[] imageInByteArray = baos.toByteArray();
        baos.close();
        return DatatypeConverter.printBase64Binary(imageInByteArray);
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    private void updateCanvasId() {
        canvasId = "" + canvasIdInt++;
    }

    /**
     * Return the next versionId string unique among versions for this photo
     * @return string
     */
    private String getNextVId() {
        return "" + nextVId++;  // TODO: Consider change how to make this versionId
    }

    /**
     * An helper object for fetch result that is ready to convert to JSON response.
     */
    class FetchResult {
        String canvasId;
        transient List<User> collaborators;  // TODO: include in the result normally
        String title;
        String canvasData;  // img converted to base 64 string
        String versionId;

        void setCanvasId(String canvasId) {
            this.canvasId = canvasId;
        }

        void setCollaborators(List<User> collaborators) {
            this.collaborators = collaborators;
        }

        void setTitle(String title) {
            this.title = title;
        }

        void setCanvasData(String bytes) {
            this.canvasData = bytes;
        }

        void setVersionId(Version v) {
            this.versionId = v.getVersionId();
        }
    }
}
