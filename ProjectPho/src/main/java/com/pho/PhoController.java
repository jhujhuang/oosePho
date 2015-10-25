package com.pho;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
                phoService.register(property.getProperty("userId"), property.getProperty("password"));
                return Collections.EMPTY_MAP;
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Registration failed!");
                response.status(409);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/login", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String token = phoService.login(property.getProperty("userId"), property.getProperty("password"));
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("reason", token);
                return returnMessage;
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Login failed!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/createnewphoto", "application/json", (request, response) -> {
            try {
                response.status(201);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                String photoId = phoService.createNewPhoto(property.getProperty("userId"));
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("pId", photoId);
                return returnMessage;
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Cannot create new photo!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/listphoto", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                return phoService.listPhotosOfCurrentUser(property.getProperty("userId"));
            } catch (PhoService.PhoServiceException ex) {
                logger.error("Cannot list all photos!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                phoService.authenticate(property.getProperty("userId"), property.getProperty("token"));
                return phoService.joinEditingSession(property.getProperty("userId"), request.params(":pId"));
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id!");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/edittitle", "application/json", (request, response) -> {
            try {
                response.status(200);
                Properties property = new Gson().fromJson(request.body(), Properties.class);
                phoService.authenticate(property.getProperty("userId"), property.getProperty("token"));
                phoService.editPhotoTitle(request.params(":pId"), property.getProperty("title"));
                return Collections.EMPTY_MAP;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id!");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token!");
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
                logger.error("Invalid photo Id!");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });

        post(API_CONTEXT + "/edit/:pId/fetch", "application/json", (request, response) -> {
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
                String newCanvasId = phoService.edit(userId, photoId, canvasId, editType, paramMap);
                Map<String, String> returnMessage = new HashMap<>();
                returnMessage.put("canvasId", newCanvasId);
                return returnMessage;
            } catch (PhoService.InvalidPhotoIdException ex) {
                logger.error("Invalid photo Id!");
                response.status(404);
                return createFailureContent(ex.getMessage());
            } catch (PhoService.InvalidTokenException ex) {
                logger.error("Invalid token!");
                response.status(401);
                return createFailureContent(ex.getMessage());
            }
        });



    }

    private Map<String, String> createFailureContent(String reason) {
        Map<String, String> response = new HashMap<>();
        response.put("reason", message);
        return response;
    }
}
