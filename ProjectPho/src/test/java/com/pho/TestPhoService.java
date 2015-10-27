package com.pho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jhuang on 10/26/15.
 */
public class TestPhoService {

    private static final int TEST_NUM = 7;

    PhoService phoService;

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
        String pId = phoService.createNewPhoto(userId);
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
            pIds[i] = phoService.createNewPhoto(userId);
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

        String pId = phoService.createNewPhoto(userId);
        phoService.joinEditingSession(userId, pId);
    }

    @Test(expected = PhoService.InvalidPhotoIdException.class)
    public void testJoinEditingSessionFailed() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId);
        String wrongId = "csf";
        assert(!pId.equals(wrongId));
        phoService.joinEditingSession(userId, wrongId);
    }

    // TODO: more tests
}
