package com.pho;

import java.util.List;

/**
 * Editing session.
 */
public class EditingSession {
    private Photo photo;
    private static String canvasId;
    private List<User> collaborators;

    /**
     * Constructor for the EditingSessions class
     * @param photo the photo used in this editing session
     */
    public EditingSession(Photo photo) {
        this.photo = photo;
    }

    public void addCollaborator(User collaborator) {
        collaborators.add(collaborator);
    }

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

    public String getPhotoId() {
        return photo.getPhotoId();
    }

}
