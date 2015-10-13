package com.pho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.post;

/**
 * The controller of the MVC model.
 */
public class PhoController {

    private static final String API_CONTEXT = ""; // TODO: confirm

    private final PhoService PhoService;

    private final Logger logger = LoggerFactory.getLogger(PhoController.class);

    /**
     * Initializes the controller with PhoService
     * @param PhoService a PhoService instance
     */
    public PhoController(PhoService PhoService) {
        this.PhoService = PhoService;
        setupEndpoints();
    }

    /**
     * Sets up the endpoints.
     */
    private void setupEndpoints() {
        // TODO: implement
    }
}
