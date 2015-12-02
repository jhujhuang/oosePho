package com.pho;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Editing session.
 */
public class EditingSession {
    private Photo photo;
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
        // TODO: initialize canvasId
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
        result.setCanvasData(canvas);
        // TODO: Do we need versionId fetched?
        Version currentVersion = photo.getCurrentVersion();
        result.setVersionId(currentVersion);
        return result;
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

        /**
         * Get the image in the form of base 64 bytes.
         * @return base 64 string
         * @throws IOException
         */
        private String getImageBytes(BufferedImage img) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            byte[] imageInByteArray = baos.toByteArray();
            baos.close();
            return DatatypeConverter.printBase64Binary(imageInByteArray);
        }

        void setCanvasId(String canvasId) {
            this.canvasId = canvasId;
        }

        void setCollaborators(List<User> collaborators) {
            this.collaborators = collaborators;
        }

        void setTitle(String title) {
            this.title = title;
        }

        void setCanvasData(BufferedImage img) throws IOException {
            this.canvasData = getImageBytes(img);
        }

        void setVersionId(Version v) {
            this.versionId = v.getVersionId();
        }
    }
}
