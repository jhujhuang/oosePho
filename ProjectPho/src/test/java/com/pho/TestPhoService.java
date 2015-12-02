package com.pho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TestPhoService {

    private static final int TEST_NUM = 7;

    PhoService phoService;
    BufferedImage testImg = new BufferedImage(2, 2, 1);

    @Before
    public void setup() throws PhoService.PhoServiceException {
        DataSource mockDataSource = mock(DataSource.class);
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
    public void testCreatePhoto() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");
        Map<String, Map<String, String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(0, map.get("photos").size());
        String pId = phoService.createNewPhoto(userId, testImg);
        map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(1, map.get("photos").size());
        assertTrue(map.get("photos").containsKey(pId));
    }

    @Test
    public void testListPhotosOfCurrentUserInitial() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");
        Map<String, Map<String, String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(0, map.get("photos").size());
    }

    @Test
    public void testListPhotosOfCurrentUserAdded() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");
        int num = TEST_NUM;
        String[] pIds = new String[num];
        for (int i = 0; i < num; i++) {
            pIds[i] = phoService.createNewPhoto(userId, testImg);
        }
        Map<String, Map<String, String>> map = phoService.listPhotosOfCurrentUser(userId);
        assertEquals(num, map.get("photos").size());
        for (int i = 0; i < num; i++) {
            assertTrue(map.get("photos").containsKey(pIds[i]));
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

    @Test
    public void testFetch() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException, IOException {
        String userId = "scott";
        phoService.register(userId, "password");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImg, "jpg", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        String s = DatatypeConverter.printBase64Binary(bytes);

        String pId = phoService.createNewPhoto(userId, testImg);
        EditingSession.FetchResult response = phoService.fetch(pId);
        assertTrue(response.canvasData != null);
        assertEquals(s, response.canvasData);
        assertEquals("0", response.versionId);
    }

    @Test(expected = PhoService.InvalidPhotoIdException.class)
    public void testFetchFailed() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        String wrongId = "csf";
        assert(!pId.equals(wrongId));
        phoService.fetch(wrongId);
    }


    @Test
    public void testEdit() throws PhoService.PhoServiceException,
            PhoService.InvalidPhotoIdException, PhoService.PhoSyncException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        EditingSession.FetchResult fetchResult = phoService.fetch(pId);
        String oldCanvasId = fetchResult.canvasId;

        String newCanvasId = phoService.edit(userId, pId, oldCanvasId, "BlurFilter", Collections.EMPTY_MAP);
        // Check that canvas id has been updated
        assertNotEquals(newCanvasId, oldCanvasId);
        // That image has been changed is checked in TestFilters

        // Other filters do not fail:
        HashMap<String, Double> changeContrastParams = new HashMap<>();
        changeContrastParams.put("value", 0.5);
        newCanvasId = phoService.edit(userId, pId, newCanvasId, "ChangeContrastFilter", changeContrastParams);
        phoService.edit(userId, pId, newCanvasId, "EdgeDetectionFilter", Collections.EMPTY_MAP);
    }

    @Test(expected = PhoService.InvalidPhotoIdException.class)
    public void testEditInvalidPhotoId() throws PhoService.PhoServiceException,
            PhoService.InvalidPhotoIdException, PhoService.PhoSyncException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        EditingSession.FetchResult fetchResult = phoService.fetch(pId);
        String oldCanvasId = fetchResult.canvasId;

        String wrongId = "csf";
        assert(!pId.equals(wrongId));
        phoService.edit(userId, wrongId, oldCanvasId, "BlurFilter", Collections.EMPTY_MAP);
    }

    @Test(expected = PhoService.PhoSyncException.class)
    public void testEditOutOfDate() throws PhoService.PhoServiceException,
            PhoService.PhoSyncException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        EditingSession.FetchResult fetchResult = phoService.fetch(pId);
        String oldCanvasId = fetchResult.canvasId;

        phoService.edit(userId, pId, oldCanvasId, "BlurFilter", Collections.EMPTY_MAP);
        phoService.edit(userId, pId, oldCanvasId, "BlurFilter", Collections.EMPTY_MAP);
    }

    @Test(expected = PhoService.PhoServiceException.class)
    public void testEditWrongFilterType() throws PhoService.PhoSyncException,
            PhoService.PhoServiceException, PhoService.InvalidPhotoIdException {
        String userId = "scott";
        phoService.register(userId, "password");

        String pId = phoService.createNewPhoto(userId, testImg);
        EditingSession.FetchResult fetchResult = phoService.fetch(pId);
        String oldCanvasId = fetchResult.canvasId;

        phoService.edit(userId, pId, oldCanvasId, "NotAFilter", Collections.EMPTY_MAP);
    }

    // TODO: more tests
}
