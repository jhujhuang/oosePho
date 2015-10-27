package com.pho;

import java.util.ArrayList;
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
}
