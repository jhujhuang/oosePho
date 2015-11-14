package com.pho;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        assertEquals("Fail to register", 201, r.httpStatus);
        r = request("POST", "/register", content);
        assertEquals("Fail to recognize existing accounts", 409, r.httpStatus);
    }

    @Test
    public void testCreateNewPhoto() throws Exception {
        Map<String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // Create a new photo
        Response r = multipartRequest("/scott/createnewphoto", "test.jpg");
        assertEquals("Fail to create new photo", 201, r.httpStatus);
        Properties property = new Gson().fromJson(r.content, Properties.class);
        String pId = property.getProperty("pId");
        // TODO: fetch and check image data
    }

    @Test
    public void testListPhotos() throws Exception {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);

        Type listType = new TypeToken<Map<String, List<String>>>() {}.getType();

        // List photos of user
        Response r = request("POST", "/listphotos", content);
        assertEquals("Fail to list photos when no photos", 200, r.httpStatus);
        Map<String, List<String>> listResult = new Gson().fromJson(r.content, listType);
        assertEquals(0, listResult.get("photos").size());

        // Create a new photo
        Response pResponse = multipartRequest("/scott/createnewphoto", "test.jpg");
        Properties property = new Gson().fromJson(pResponse.content, Properties.class);
        String pId = property.getProperty("pId");

        // List again and check pId
        r = request("POST", "/listphotos", content);
        assertEquals("Fail to list photos when no photos", 200, r.httpStatus);
        listResult = new Gson().fromJson(r.content, listType);
        assertEquals(1, listResult.get("photos").size());
        assertEquals(pId, listResult.get("photos").get(0));
    }

    @Test
    public void testJoinEditSession() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // Create a new photo
        Response pResponse = multipartRequest("/scott/createnewphoto", "test.jpg");
        Properties property = new Gson().fromJson(pResponse.content, Properties.class);
        String pId = property.getProperty("pId");

        // Join editing session
        Response joinResponse = request("POST", "/edit/" + pId, content);
        assertEquals("Fail to join editing session", 200, joinResponse.httpStatus);

        // Join non-existing editing session
        joinResponse = request("POST", "/edit/csf", content);
        assertEquals("Fail to recognize non-existing pId", 404, joinResponse.httpStatus);
    }


    @Test
    public void testChangePhotoTitle() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);

        // Create a new photo
        Response pResponse = multipartRequest("/scott/createnewphoto", "test.jpg");
        Properties property = new Gson().fromJson(pResponse.content, Properties.class);
        String pId = property.getProperty("pId");

        // Change title
        String newTitle = "New Title";
        content = new HashMap<>();
        content.put("title", newTitle);
        Response titleResponse = request("POST", "/edit/" + pId + "/edittitle", content);
        assertEquals("Fail to change title", 200, titleResponse.httpStatus);

        // Non-existing photo
        titleResponse = request("POST", "/edit/csf/edittitle", content);
        assertEquals("Fail to recognize non-existing pId", 404, titleResponse.httpStatus);
    }

    @Test
    public void testEdit() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // TODO
    }

    @Test
    public void testFetch() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);

        // Create a new photo
        Response pResponse = multipartRequest("/scott/createnewphoto", "test.jpg");
        Properties property = new Gson().fromJson(pResponse.content, Properties.class);
        String pId = property.getProperty("pId");

        // Fetch
        // TODO: WHY /edit/8Q/fetch not found?
        Response fetchResult = request("GET", "/edit/" + pId + "/fetch", Collections.EMPTY_MAP);
        System.out.println(fetchResult.content);
        System.out.println(fetchResult.httpStatus);
        Properties fetched = new Gson().fromJson(fetchResult.content, Properties.class);
        String base64 =  fetched.getProperty("canvasData");
        // TODO
    }

    @Test
    public void testMakeComment() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // TODO
    }

    @Test
    public void testSeeRevisions() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // TODO
    }

    @Test
    public void testSaveVersion() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // TODO
    }

    @Test
    public void testRevertVersion() {
        Map <String, String> content = new HashMap<String, String>();
        content.put("userId", "scott");
        content.put("password", "oose");
        request("POST", "/register", content);
        // TODO
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
            if (http.getResponseCode() < 400) {
                String responseBody = IOUtils.toString(http.getInputStream());
                return new Response(http.getResponseCode(), responseBody);
            } else {
                String responseBody = IOUtils.toString(http.getErrorStream());
                return new Response(http.getResponseCode(), responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    private Response multipartRequest(String path, String filename) {
        try {
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            FileBody fileBody = new FileBody(new File(filename));
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
            multipartEntity.addPart("file", fileBody);

            http.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
            OutputStream out = http.getOutputStream();
            try {
                multipartEntity.writeTo(out);
            } finally {
                out.close();
            }
            if (http.getResponseCode() < 400) {
                String responseBody = IOUtils.toString(http.getInputStream());
                return new Response(http.getResponseCode(), responseBody);
            } else {
                String responseBody = IOUtils.toString(http.getErrorStream());
                return new Response(http.getResponseCode(), responseBody);
            }
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




