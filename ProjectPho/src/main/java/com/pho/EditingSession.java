package com.pho;

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
public class EditingSession {
    private Photo photo;
    private int canvasIdInt;
    private String canvasId;
    private List<User> collaborators;
    private BufferedImage canvas;

    /**
     * Constructor for the EditingSessions class
     * @param photo the photo used in this editing session
     */
    public EditingSession(Photo photo) {
        this.photo = photo;
        this.canvas = photo.getCurrentVersion().getImage();
        collaborators = new ArrayList<>();
        // Initialize canvasId
        canvasIdInt = 0;
        updateCanvasId();
    }

    private void updateCanvasId() {
        canvasId = "" + canvasIdInt++;
    }

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
     * Get the photo associated with this editing session
     * @return Photo
     */
    Photo getPhoto() {
        return photo;
    }

    // TODO: Add docstring
    public String edit(String userId, String editType, Map<String, String> params) {
        // TODO: make changes and store new canvas image
        updateCanvasId();
        return canvasId;
    }

    /**
     * Retrieves the result for fetch
     * @return FetchResult object
     * @throws IOException
     */
    public FetchResult getFetchResults() throws IOException {
        FetchResult result = new FetchResult();
        result.setCanvasId(canvasId);
        result.setCollaborators(collaborators);
        result.setTitle(photo.getTitle());
        result.setCanvasData(getImageBytes());
        // TODO: Do we need versionId fetched?
        Version currentVersion = photo.getCurrentVersion();
        result.setVersionId(currentVersion);
        return result;
    }

    /**
     * Get the image in the form of base 64 bytes.
     * @return base 64 string
     * @throws IOException
     */
    public String getImageBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "jpg", baos);
        baos.flush();
        byte[] imageInByteArray = baos.toByteArray();
        baos.close();
        return DatatypeConverter.printBase64Binary(imageInByteArray);
    }

    /**
     * An helper object for fetch result that is ready to convert to JSON response.
     */
    class FetchResult {
        String canvasId;
        List<User> collaborators;
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
