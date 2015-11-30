package com.pho;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;
import spark.utils.IOUtils;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPhoController {

    static final String TEST_IMG_FILE = "test.jpg";
    static final int TEST_HEIGHT = 600;
    static final int TEST_WIDTH = 800;

    static final String TEST_USERID = "scott";
    static final String TEST_PASSWORD = "oose";

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
    public void testRegister() {
        Map<String, String> content = new HashMap<>();
        content.put("userId", TEST_USERID);
        content.put("password", TEST_PASSWORD);
        Response r = request("POST", "/register", content);
        assertEquals("Fail to register", 201, r.httpStatus);
        r = request("POST", "/register", content);
        assertEquals("Fail to recognize existing accounts", 409, r.httpStatus);
    }

    @Test
    public void testCreateNewPhoto() throws IOException {
        registerUser();

        // Create a new photo
        Response r = multipartRequest("/createnewphoto", TEST_IMG_FILE);
        assertEquals("Fail to create new photo", 201, r.httpStatus);
        Properties property = new Gson().fromJson(r.content, Properties.class);
        String pId = property.getProperty("pId");

        // Fetch and check image data
        Type fetchType = new TypeToken<EditingSession.FetchResult>() {}.getType();
        Response fetchResult = request("GET", "/edit/" + pId + "/fetch", null);
        EditingSession.FetchResult fetched = new Gson().fromJson(fetchResult.content, fetchType);

        String base64 =  fetched.canvasData;
        byte[] bytes = DatatypeConverter.parseBase64Binary(base64);
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage imgFetched = ImageIO.read(in);
        // Currently we just check the height and width of image
        assertEquals(TEST_HEIGHT, imgFetched.getHeight());
        assertEquals(TEST_WIDTH, imgFetched.getWidth());
    }

    @Test
    public void testListPhotos() {
        registerUser();
        Type listType = new TypeToken<Map<String, List<String>>>() {}.getType();

        // List photos of user
        Map<String, String> content = new HashMap<>();
        content.put("userId", TEST_USERID);
        Response r = request("POST", "/listphotos", content);
        assertEquals("Fail to list photos when no photos", 200, r.httpStatus);
        Map<String, List<String>> listResult = new Gson().fromJson(r.content, listType);
        assertEquals(0, listResult.get("photos").size());

        String pId = createNewPhoto();

        // List again and check pId
        r = request("POST", "/listphotos", content);
        assertEquals("Fail to list photos when has photos", 200, r.httpStatus);
        listResult = new Gson().fromJson(r.content, listType);
        assertEquals(1, listResult.get("photos").size());
        assertEquals(pId, listResult.get("photos").get(0));
    }

    @Test
    public void testJoinEditSession() {
        registerUser();
        String pId = createNewPhoto();

        // Join editing session
        Map<String, String> content = new HashMap<>();
        content.put("userId", TEST_USERID);
        Response joinResponse = request("POST", "/edit/" + pId, content);
        assertEquals("Fail to join editing session", 200, joinResponse.httpStatus);

        // Join non-existing editing session
        joinResponse = request("POST", "/edit/csf", content);
        assertEquals("Fail to recognize non-existing pId", 404, joinResponse.httpStatus);
    }

    @Test
    public void testChangePhotoTitle() {
        registerUser();
        String pId = createNewPhoto();

        // Change title
        String newTitle = "New Title";
        Map<String, String> content = new HashMap<>();
        content.put("title", newTitle);
        Response titleResponse = request("POST", "/edit/" + pId + "/edittitle", content);
        assertEquals("Fail to change title", 200, titleResponse.httpStatus);

        // Fetch and check new title
        Type fetchType = new TypeToken<EditingSession.FetchResult>() {}.getType();
        Response fetchResult = request("GET", "/edit/" + pId + "/fetch", null);
        EditingSession.FetchResult fetched = new Gson().fromJson(fetchResult.content, fetchType);
        assertEquals(newTitle, fetched.title);

        // Non-existing photo
        titleResponse = request("POST", "/edit/csf/edittitle", content);
        assertEquals("Fail to recognize non-existing pId", 404, titleResponse.httpStatus);
    }

    @Test
    public void testEdit() {
        registerUser();
        // TODO
    }

    @Test
    public void testFetch() throws IOException {
        registerUser();
        String pId = createNewPhoto();

        Type fetchType = new TypeToken<EditingSession.FetchResult>() {}.getType();

        // Fetch
        Response fetchResult = request("GET", "/edit/" + pId + "/fetch", null);
        assertEquals(200, fetchResult.httpStatus);

        // Check fetch results
        EditingSession.FetchResult fetched = new Gson().fromJson(fetchResult.content, fetchType);

        String base64 =  fetched.canvasData;
        byte[] bytes = DatatypeConverter.parseBase64Binary(base64);
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage imgFetched = ImageIO.read(in);
        // Currently we just check the height and width of image
        assertEquals(TEST_HEIGHT, imgFetched.getHeight());
        assertEquals(TEST_WIDTH, imgFetched.getWidth());
        // TODO: Other fetched results checks (some already in other tests)

        // Fetch again of a non-existing photo
        fetchResult = request("GET", "/edit/csf/fetch", null);
        assertEquals(404, fetchResult.httpStatus);
    }

    @Test
    public void testMakeComment() {
        registerUser();
        // TODO
    }

    @Test
    public void testSeeRevisions() {
        registerUser();
        // TODO
    }

    @Test
    public void testSaveVersion() {
        registerUser();
        // TODO
    }

    @Test
    public void testRevertVersion() {
        registerUser();
        // TODO
    }


    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//

    /** Register a user. **/
    private void registerUser() {
        Map<String, String> content = new HashMap<>();
        content.put("userId", TEST_USERID);
        content.put("password", TEST_PASSWORD);
        request("POST", "/register", content);
    }

    /** Create a photo and return the photo Id. */
    private String createNewPhoto() {
        Response pResponse = multipartRequest("/createnewphoto", TEST_IMG_FILE);
        return new Gson().fromJson(pResponse.content, Properties.class).getProperty("pId");
    }

    private Response request(String method, String path, Object content) {
        try {
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, "/api" + path);
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
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, "/api" + path);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("file", new File(filename));
            multipartEntityBuilder.addTextBody("userId", TEST_USERID);  // Add userId to request
            HttpEntity httpEntity = multipartEntityBuilder.build();

            http.setRequestProperty("Content-Type", ContentType.get(httpEntity).toString());
            OutputStream out = http.getOutputStream();
            try {
                httpEntity.writeTo(out);
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



