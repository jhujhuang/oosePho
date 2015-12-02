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
    public void edit(String userId, String editType, Map<String, Double> params)
            throws PhoService.PhoServiceException {
        Filter f;
        try {
            f = Filter.getFilter(editType, params);
        } catch (Filter.UnknownFilterException e) {
            throw new PhoService.PhoServiceException("Unknown filterType", e);
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
