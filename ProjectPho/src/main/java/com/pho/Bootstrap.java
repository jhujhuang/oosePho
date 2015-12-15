package com.pho;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Bootstrap.
 */
public class Bootstrap {

    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        //Check if the database file exists in the current directory. Abort if not
        DataSource dataSource = configureDataSource();
        if (dataSource == null) {
            System.out.printf("Could not find Pho.db in the current directory (%s). Terminating\n",
                    Paths.get(".").toAbsolutePath().normalize());
            System.exit(1);
        }

        //Specify the IP address and Port at which the server should be run
        ipAddress(IP_ADDRESS);
        port(PORT);
        // port(getHerokuAssignedPort());

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service
        try {
            PhoService model = new PhoService(dataSource);
            new PhoController(model);
        } catch (PhoService.PhoServiceException ex) {
            logger.error("Failed to create a Service instance. Aborting");
        }
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
     * Check if the database file exists in the current directory. If it does
     * create a DataSource instance for the file and return it.
     * @return javax.sql.DataSource corresponding to the Game database
     */
    private static DataSource configureDataSource() {
        Path GamePath = Paths.get(".", "pho.db");
        if ( !(Files.exists(GamePath) )) {
            try { Files.createFile(GamePath); }
            catch (java.io.IOException ex) {
                logger.error("Failed to create pho.db file in current directory. Aborting");
            }
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:pho.db");
        return dataSource;

    }
}
