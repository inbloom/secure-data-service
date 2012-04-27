package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.tenant.TenantDA;

/**
 * tests for LandingZoneManager
 *
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LandingZoneManagerTest {

    @Autowired
    private LocalFileSystemLandingZone lz;

    @Mock
    private TenantDA mockTenantDA;

    @InjectMocks
    private LandingZoneManager landingZoneManager;

    private String topLevelDir;

    @Before
    public void setup() {
        landingZoneManager = new LandingZoneManager();

        MockitoAnnotations.initMocks(this);

        topLevelDir = lz.getLZId();

        String localhost = "";
        try {
            localhost = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            fail("Failed determining local host name prior to test: " + e.getMessage());
        }
        List<String> multiplePaths = getMultipleMockedPaths();
        Mockito.when(mockTenantDA.getLzPaths(localhost)).thenReturn(multiplePaths);
        landingZoneManager.setSingleLandingZoneDir(lz.getLZId());
    }


    @Test
    public void testGetMultipleLandingZones() {
        landingZoneManager.setMultipleLandingZonesEnabled(true);

        List<LocalFileSystemLandingZone> lzList = landingZoneManager.getLandingZones();
        List<String> expectedPaths = getMultipleMockedPaths();

        assertEquals("Expecting different number of returned landing zones", lzList.size(), expectedPaths.size());

        int i = 0;
        for (LocalFileSystemLandingZone currentLz : lzList) {
            String expectedPath = new File(expectedPaths.get(i++)).getAbsolutePath();
            String actualPath = currentLz.getDirectory().getAbsolutePath();
            assertEquals("Expecting different path", expectedPath, actualPath);
        }
    }

    @Test
    public void testGetSingleLandingZone() {
        landingZoneManager.setMultipleLandingZonesEnabled(false);

        List<LocalFileSystemLandingZone> lzList = landingZoneManager.getLandingZones();
        String expectedPath = getSingleMockedPath();

        assertEquals("Expecting different number of returned landing zones", lzList.size(), 1);

        assertEquals("Expecting different path", lzList.get(0).getDirectory().getAbsolutePath(), expectedPath);
    }

    private List<String> getMultipleMockedPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(new File(topLevelDir + File.separator + "dir1").getAbsolutePath());
        paths.add(new File(topLevelDir + File.separator + "dir2").getAbsolutePath());
        paths.add(new File(topLevelDir + File.separator + "dir3").getAbsolutePath());
        return paths;
    }

    private String getSingleMockedPath() {
        return lz.getDirectory().getAbsolutePath();
    }
}
