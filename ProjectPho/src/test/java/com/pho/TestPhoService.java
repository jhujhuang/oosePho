package com.pho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestPhoService {

    private static final int TEST_NUM = 7;

    PhoService phoService;
    BufferedImage testImg = new BufferedImage(2, 2, 1);

    @Before
    public void setup() throws PhoService.PhoServiceException {
        DataSource mockDataSource = new SQLiteDataSource();  // TODO: Change to actual mock
        phoService = new PhoService(mockDataSource);
    }

    @After
    public void teardown() {
        // Does nothing for now
    }

    @Test
    public void testAddUserSuccess() throws PhoService.PhoServiceException {
        phoService.register("scott", "oose");
    }

    @Test(expected = PhoService.PhoServiceException.class)
    public void testAddUserDuplicate() throws PhoService.PhoServiceException {
        String userId = "scott";
        phoService.register(userId, "0");
        phoService.register(userId, "1");
    }

    @Test
    public void testCreatePhoto() throws PhoService.PhoServiceException {
        String userId = "scott";
        phoService.register(userId, "password");
        Map<String, List<String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(0, map.get("photos").size());
        String pId = phoService.createNewPhoto(userId, testImg);
        map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(1, map.get("photos").size());
        assertEquals(pId, map.get("photos").get(0));
    }

    @Test
    public void testListPhotosOfCurrentUserInitial() throws PhoService.PhoServiceException {
        String userId = "scott";
        phoService.register(userId, "password");
        Map<String, List<String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(0, map.get("photos").size());
    }

    @Test
    public void testListPhotosOfCurrentUserAdded() throws PhoService.PhoServiceException {
        String userId = "scott";
        phoService.register(userId, "password");
        int num = TEST_NUM;
        String[] pIds = new String[num];
        for (int i = 0; i < num; i++) {
            pIds[i] = phoService.createNewPhoto(userId, testImg);
        }
        Map<String, List<String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(num, map.get("photos").size());
        for (int i = 0; i < num; i++) {
            assertEquals(pIds[i], map.get("photos").get(i));
        }
    }

    @Test
    public void testJoinEditingSession() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        phoService.joinEditingSession(userId, pId);
    }

    @Test(expected = PhoService.InvalidPhotoIdException.class)
    public void testJoinEditingSessionFailed() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        String wrongId = "csf";
        assert(!pId.equals(wrongId));
        phoService.joinEditingSession(userId, wrongId);
    }

    @Test
    public void testGetRevisionsInitial() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");
        String pId = phoService.createNewPhoto(userId, testImg);

        Map<String, List<Version>> map = phoService.getRevisions(pId);
        assertEquals(1, map.get("versions").size());
        // Check that the image stored in the first version is correct
        assertEquals(testImg, map.get("versions").get(0).getImage());
        // Check that the author of the first version is the creator
        assertEquals(userId, map.get("versions").get(0).getUserId());
    }

    // TODO: more tests
}
