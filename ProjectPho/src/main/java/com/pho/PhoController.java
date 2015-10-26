package com.pho;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String API_CONTEXT = ""; // TODO: confirm

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
        });

        post(API_CONTEXT + "/login", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String password = property.getProperty("password");
                String token = phoService.login(userId, password);
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("token", token);
                return returnMessage;
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Wrong userId/password");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/createnewphoto", "application/json", (request, response) -> {
            try {
                response.status(201);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                phoService.authenticate(userId, token);
                String photoId = phoService.createNewPhoto(userId);
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("pId", photoId);
                return returnMessage;
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/listphotos", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                phoService.authenticate(userId, token);
                return phoService.listPhotosOfCurrentUser(userId);
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                phoService.authenticate(userId, token);
                phoService.joinEditingSession(userId, photoId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/edittitle", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                phoService.authenticate(userId, token);
                phoService.editPhotoTitle(request.params(":pId"), property.getProperty("title"));
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/change", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String canvasId = property.getProperty("canvasId");
                String editType = property.getProperty("editType");
                String moreParams = property.getProperty("moreParams");
                HashMap<String, String> paramMap = new Gson().fromJson(moreParams, HashMap.class);
                String photoId = request.params(":pId");
                phoService.authenticate(userId, token);
                phoService.edit(userId, photoId, canvasId, editType, paramMap);

                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoSyncException ex) {
                logger.error("Canvas out of date");
                response.status(410);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoServiceException ex) {
                if (ex.getMessage().equals("Invalid editing type")) {
                    logger.error("Invalid editing type");
                } else {
                    logger.error("Invalid editing parameters");
                }
                response.status(400);
                return createFailureContent(ex.getMessage());
            }
        });

        get(API_CONTEXT + "/edit/:pId/fetch", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                phoService.authenticate(userId, token);
                return phoService.fetch(photoId);
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/comment", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                String comment = property.getProperty("comment");
                phoService.authenticate(userId, token);
                phoService.comment(userId, photoId, comment);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/versions", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                phoService.authenticate(userId, token);
                return phoService.getRevisions(photoId);
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/versions/revert", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                String versionId = property.getProperty("versionId");
                phoService.authenticate(userId, token);
                phoService.revertToSelectedVersion(photoId, versionId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Invalid version Id");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/save", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String userId = property.getProperty("userId");
                String token = property.getProperty("token");
                String photoId = request.params(":pId");
                String canvasId = property.getProperty("canvasId");
                phoService.authenticate(userId, token);
                phoService.saveVersion(userId, photoId, canvasId);
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token");
                response.status(401);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.PhoSyncException ex) {
                logger.error("Canvas out of date");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

    }

    private Map<String, String> createFailureContent(String reason) {
        Map<String, String> response = new HashMap<>();
        response.put("reason", reason);
        return response;
    }
}
