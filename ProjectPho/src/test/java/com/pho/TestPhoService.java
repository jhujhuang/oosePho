package com.pho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testFetch() throws PhoService.PhoServiceException, PhoService.InvalidPhotoIdException, IOException {
        String userId = "scott";
        phoService.register(userId, "password");

        String s = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAAEJklEQVQ4Ea1UW2xUVRRd9zXTefQ5bZHSYKUOg3WIDZE6LbFCqEowFDVggqKS6I/G+OGXUUz0w8iXIgpNhDQG5ceg0JZRDBqI2jJtU61UW8uElNLSQplOh+m00+l9HPe+01GJ+OdO1r377sc6+5y9zwX+Z5H+iy9QhOCWO/HEAytRX1mqVnLceMwY776C86dHcWI4gd9ul/svwpI8lL+5Hvv2NJc+m1+7SVVWboDkqbBzxdwEzCudmO0/a3zaHvvs3V68Hl/A1O2I2SbdVYA13S/IUf3rJiFip4SY6xciGRFi5ocsWGcb+TiGYzmHc3OkOUUqdqL8zHNypPaZLVXK2peBzCxgzQNC5GKzb4lSZDfgzIc5cAj9x05ffvioFZrJ2JUKZSlaea9RamnetapRDe4C0hOATjuxiNQk6EnA4AVSWZs+TQtOQ/YFUO6KFrmuJZZ/exltxGUTyv4i1La86Drguq9OgqxRMJFpaWCRiBSqMkNvk8hU0nO2dJxsC5BdMmocY8Eve4x2Os/rMrEq26qlp92VXhmSAcyPUGIMg92/4vndn+BcRyd9X7fBOtvYxzF2rGKAc5mDuVR6qPXVakgpcNDq1+iTKoSFk8d/R4XHQrjtAjbeT9slCbeN2LaTx/tQs+ZeYIHr0cG5zIE+XWWLVuFTyqExISUuxiDicRSv8OLtD4IIrCtFKmHZYP0dsrGPYzjWbp7DAZuDuLjLJZ0vOSKh7WV+2Uk9cpiAlzrroOIti0C2DNlY2C+TznXM0/GkKF1XYJE/0n4juqFlMcRbFlfjZgx6yg8XsPetOfh8BgJ3A8vuAIpKgDyy0ykgTX1KUGGTk8DgH8Rlatj7hgeYFSCOG8zFhHpkxLrweEavl70GdHch5IeOIu5OYyw2CvPSFCxjPju5mgeaexncNVVAqQGzZw/gmYc5poE4BpiLAM1fjObkQacpBrziu/0QOxvrxKGD74sfe38Rk0lT6EKIDGE8nhFnu7rFgf37xJMNa0X3YRr7fq9IfuQ0mYO5uEIjOoO+1g49/Ooqbdvm7V6MDvVg5HAPJr4AvqLjsrh1JHx81HiIBLCzCah7hJpDdbWG9TBzUIiRu3oO+ik0fP+K+nntDtcK0L/lm2M6UkMGCvJMWjZ7/XRdRjIjo7hWRdNTNF40sv0n0lc3f2zspqHuIsLF3NWz0gZunhm0og+6rNDyMqXQv0mDu0rFTUVBmsZVL9TgqtYQ3OrAugba2LCFnzsWxnYcMV6bSOE8kdE1ulW42jKfC49+uFU+NdvqNESXR4hLBUKMLYH1nzxi9ojT4BiO5RxCbqd/K0vc7MgnVAZ8WP/YamljaLV8T2W5VMr+8SkRi1y0hsIXxbnhafSyicDXKHsmpPzFTPo/hdtQRCgm0KCBrpEti/ScI8wQqDU8nbfKny8J3bgvSgspAAAAAElFTkSuQmCC";
        InputStream in = new ByteArrayInputStream(s.getBytes());
        BufferedImage img = ImageIO.read(in);

        String pId = phoService.createNewPhoto(userId, img);
        EditingSession.FetchResult response = phoService.fetch(pId);
        assertTrue(response.canvasData != null);  // TODO: assert bytes correct
        assertEquals(s, response.canvasData);
        System.out.print(response.canvasData);
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

    // TODO: more tests
}
