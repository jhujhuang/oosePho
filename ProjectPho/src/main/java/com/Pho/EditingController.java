package com.Pho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.post;

public class EditingController {

    private static final String API_CONTEXT = "/Pho/api/sessions";

    private final EditingService editingService;

    private final Logger logger = LoggerFactory.getLogger(EditingController.class);
    public EditingController(EditingService editingService) {
        this.editingService = editingService;
        setupEndpoints();
    }
    private void setupEndpoints() {
        // start a game
        post(API_CONTEXT, "application/json", (request, response) -> {

                logger.error("Failed to create new game");
                System.out.println("Error occurred when starting game: failed to create new game!");
                return Collections.EMPTY_MAP;
        }, new JsonTransformer());

    }
}
