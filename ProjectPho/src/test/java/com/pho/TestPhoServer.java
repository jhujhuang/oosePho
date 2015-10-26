package com.pho;

import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Spark;
import spark.utils.IOUtils;

import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.HttpURLConnection;

import com.pho.PhoService;
import com.pho.filters.*;

import org.junit.*;

import javax.sql.DataSource;

import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

public class TestPhoServer {

    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//

    @Before
    public void setup() throws Exception {
        //Clear the database and then start the server
        //clearDB();

        //Start the main server
        Bootstrap.main(null);
        Spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        //Stop the server
        //clearDB();
        Spark.stop();
    }

    //------------------------------------------------------------------------//
    // Tests
    //------------------------------------------------------------------------//

    @Test
    public void testRegister() throws Exception {
        Map<String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        Response r = request("POST", "/register", content);
        assertEquals("Fail to register", 200, r.httpStatus);
        r = request("POST", "/register", content);
        assertEquals("Fail to recognize existing accounts", 409, r.httpStatus);
    }

    @Test
    public void testLogin() throws Exception {
        Map<String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        Response r = request("POST", "/login", content);
        assertEquals("Fail to login", 200, r.httpStatus);

        Map<String, String> rContent = r.getContentAsObject((new TypeToken<HashMap<String, String>>() { }). getType());
        String token = rContent.get("token");
        assertNotEquals("Token is null", null, token);

        content.clear();
        content.put("userId", "scott");
        content.put("password", "hello");
        r = request("POST", "/login", content);
        assertEquals("Fail to recognize wrong password", 401, r.httpStatus);

        content.clear();
        content.put("userId", "david");
        content.put("userId", "hello");
        r = request("POST", "/login", content);
        assertEquals("Fail to recognize wrong id", 401, r.httpStatus);
    }

    @Test
    public void testCreateNewPhoto() throws Exception {
        Map<String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        Response r = request("POST", "/login", content);
        content = r.getContentAsObject((new TypeToken<HashMap<String, String>>() { }). getType());
        content.put ("userId", "scott");
        r = request("POST", "/newphoto", content);
        assertEquals("Fail to create new photo", 200, r.httpStatus);

        String token = content.remove("token");
        content.put("token", token + "something");
        r = request("POST", "/newphoto", content);
        assertEquals("Invalid token", 401, r.httpStatus);
    }

    public void testListPhotos() throws Exception {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        //TODO
    }

    @Test
    public void testBlurFilterCircle() {
        Map<String, Double> params = new HashMap<String, Double>();
        params.put("value", 0.5);
    }
        // Response r = request("GET", "/edit/test/change", );

    @Test
    public void testBlurFilter() throws Exception {
        Map<String, Double> params = new HashMap<>();
        params.put("value", 0.5);
        Filter blurFilter = new BlurFilter(params);
        blurFilter.loadImage("test.jpg");
        BufferedImage p1 = blurFilter.getImage();
        blurFilter.applyToCircle(2, 3, 4);
        BufferedImage p2 = blurFilter.getImage();

        assertEquals(p1.getHeight(), p2.getHeight());
        assertEquals(p1.getWidth(), p2.getWidth());

        for (int x = 0; x < p1.getWidth(); x++) {
            for (int y = 0; y < p2.getHeight(); y++)
                assertEquals(p1.getRGB(x, y), p2.getRGB(x, y));
        }
    }






    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//

    private Response request(String method, String path, Object content) {
        try {
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(contentAsJson);
                output.flush();
                output.close();
            }

            String responseBody = IOUtils.toString(http.getInputStream());
            return new Response(http.getResponseCode(), responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }


    private static class Response {

        public String content;

        public int httpStatus;

        public Response(int httpStatus, String content) {
            this.content = content;
            this.httpStatus = httpStatus;
        }

        public <T> T getContentAsObject(Type type) {
            return new Gson().fromJson(content, type);
        }
    }

    //------------------------------------------------------------------------//
    // GameApp Specific Helper Methods and classes
    //------------------------------------------------------------------------//

    /*private void clearDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:game.db");

        Sql2o db = new Sql2o(dataSource);

        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS item" ;
            conn.createQuery(sql).executeUpdate();
        }
    }*/

}