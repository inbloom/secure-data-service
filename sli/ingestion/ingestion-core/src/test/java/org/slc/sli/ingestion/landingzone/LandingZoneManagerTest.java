/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
    }


    @Test
    public void testGetMultipleLandingZones() {
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

    private List<String> getMultipleMockedPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(new File(topLevelDir + File.separator + "dir1").getAbsolutePath());
        paths.add(new File(topLevelDir + File.separator + "dir2").getAbsolutePath());
        paths.add(new File(topLevelDir + File.separator + "dir3").getAbsolutePath());
        return paths;
    }

}
