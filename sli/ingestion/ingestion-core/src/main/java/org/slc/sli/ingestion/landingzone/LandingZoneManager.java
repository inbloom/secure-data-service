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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.tenant.TenantDA;

/**
 * Manages the landing zones to be monitored.
 *
 * @author vmcglaughlin
 *
 */
public class LandingZoneManager {

    @Autowired
    private TenantDA tenantDA;

    private Logger log = LoggerFactory.getLogger(ControlFileProcessor.class);

    public List<LocalFileSystemLandingZone> getLandingZones() {
        List<LocalFileSystemLandingZone> landingZoneList;
        landingZoneList = getMultipleLandingZones();
        return landingZoneList;
    }

    protected List<LocalFileSystemLandingZone> getMultipleLandingZones() {
        List<LocalFileSystemLandingZone> landingZoneList = new ArrayList<LocalFileSystemLandingZone>();
        try {

            String localhostname = null;
            //get the ingestion server host name to use for obtaining landing zones
            localhostname = java.net.InetAddress.getLocalHost().getHostName();

            List<String> lzPaths = tenantDA.getLzPaths(localhostname);
            for (String lzPath : lzPaths) {
                landingZoneList.add(new LocalFileSystemLandingZone(new File(lzPath)));
            }

        } catch (Exception e) {
            log.error("Exception encountered extracting landing zones from tenant collection:", e);
        }
        return landingZoneList;
    }

}
