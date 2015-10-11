package com.Pho;

import org.sql2o.Sql2o;

import javax.sql.DataSource;

public class EditingService {

    private Sql2o db;

    public EditingService(DataSource dataSource) throws EditingServiceException {
        // create a new database for games
        // but I couldn't think of a good way to store all the game info
        // so this database is empty
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        /*try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new GameServiceException("Failed to create schema at startup", ex);
        }*/
    }
  //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class EditingServiceException extends Exception {
        public EditingServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
