package com.pho;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static spark.Spark.get;
import static spark.Spark.post;


/**
 * The controller of the MVC model.
 */
public class PhoController {

    private static final String API_CONTEXT = "/api";  // TODO: confirm
    private static final String UPLOAD_FILE = "file";  // name of upload form

    private final PhoService phoService;

    private final Logger logger = LoggerFactory.getLogger(PhoController.class);

    /**
     * Initializes the controller with PhoService
     * @param phoService a PhoService instance
     */
    public PhoController(PhoService phoService) {
        this.phoService = phoService;
        setupEndpoints();
    }

    /**
     * Sets up the endpoints.
     */
    private void setupEndpoints() {
        // Register a new user
        post(API_CONTEXT + "/register", "application/json", (request, response) -> {
            try {
                response.status(201);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String password = property.getProperty("password");
                phoService.register(userId, password);
                return Collections.EMPTY_MAP;
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Registration failed");
                response.status(409);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Create a new photo from uploaded picture
        post(API_CONTEXT + "/createnewphoto", "multipart/form-data", (request, response) -> {
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
            request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);

            response.status(201);

            // Get file FIRST, THEN userId. TODO: Don't ask me why because I don't know
            Part file = request.raw().getPart(UPLOAD_FILE);
            String userId = request.raw().getParameter("userId");
            InputStream imageStream = file.getInputStream();
            BufferedImage img = ImageIO.read(imageStream);

            String photoId = phoService.createNewPhoto(userId, img);
            Map<String, String> returnMessage = new HashMap<>();
            returnMessage.put("pId", photoId);
            return returnMessage;
        }, new JsonTransformer());

        // List all photos of the user
        post(API_CONTEXT + "/listphotos", "application/json", (request, response) -> {
            response.status(200);
            Properties property = new Gson().fromJson(request.body(), Properties.class);
            String userId = property.getProperty("userId");

            return phoService.listPhotosOfCurrentUser(userId);
        }, new JsonTransformer());

        // Join an editing session for a specific photo
        post(API_CONTEXT + "/edit/:pId", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String photoId = request.params(":pId");
                phoService.joinEditingSession(userId, photoId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Edit the title of the photo in the current editing session
        post(API_CONTEXT + "/edit/:pId/edittitle", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                phoService.editPhotoTitle(request.params(":pId"), property.getProperty("title"));
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Edit the photo (make a change)
        post(API_CONTEXT + "/edit/:pId/change", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String canvasId = property.getProperty("canvasId");
                String editType = property.getProperty("editType");
                String moreParams = property.getProperty("moreParams");
                Map<String, Double> paramMap = new Gson().fromJson(moreParams, HashMap.class);
                String photoId = request.params(":pId");
                phoService.edit(photoId, canvasId, editType, paramMap);

                // DO NOT return canvasId; rather frontend will get new info by fetching.
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoSyncException ex) {
                logger.error("Canvas out of date");
                response.status(410);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoServiceException ex) {
                if (ex.getMessage().contains("type")) {
                    logger.error("Invalid editing type");
                } else if (ex.getMessage().contains("param")) {
                    logger.error("Invalid editing parameters");
                } else {
                    logger.error(ex.getMessage());
                }
                response.status(400);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Fetch the canvas id only.
        get(API_CONTEXT + "/edit/:pId/canvasid", "application/json", (request, response) -> {
            try {
                response.status(200);
                String photoId = request.params(":pId");
                String canvasId = phoService.getCanvasId(photoId);
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("canvasId", canvasId);
                return returnMessage;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Fetch editing session status, including canvas image data
        get(API_CONTEXT + "/edit/:pId/fetch", "application/json", (request, response) -> {
            try {
                response.status(200);
                String photoId = request.params(":pId");
                Photo.FetchResult fetched = phoService.fetch(photoId);
                return fetched;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Leave a comment
        post(API_CONTEXT + "/edit/:pId/comment", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String photoId = request.params(":pId");
                String comment = property.getProperty("comment");
                phoService.comment(userId, photoId, comment);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // List all versions of the photo
        get(API_CONTEXT + "/edit/:pId/versions", "application/json", (request, response) -> {
            try {
                response.status(200);
                String photoId = request.params(":pId");
                return phoService.getRevisions(photoId);
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Revert photo to a specific version
        post(API_CONTEXT + "/edit/:pId/versions/revert", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String photoId = request.params(":pId");
                String versionId = property.getProperty("versionId");
                phoService.revertToSelectedVersion(photoId, versionId, userId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Invalid version Id");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

        // Save a version of photo
        post(API_CONTEXT + "/edit/:pId/save", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String photoId = request.params(":pId");
                String canvasId = property.getProperty("canvasId");
                phoService.saveVersion(userId, photoId, canvasId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoSyncException ex) {
                logger.error("Canvas out of date");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        }, new JsonTransformer());

    }

    private Map<String, String> createFailureContent(String reason) {
        Map<String, String> response = new HashMap<>();
        response.put("reason", reason);
        return response;
    }
}
