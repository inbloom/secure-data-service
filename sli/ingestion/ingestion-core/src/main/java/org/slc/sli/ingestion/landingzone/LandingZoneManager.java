package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Mananges the landing zones to be monitored.
 *
 * @author vmcglaughlin
 *
 */
public class LandingZoneManager {

    @Autowired
    private TenantDA tenantDA;

    private boolean multipleLandingZonesEnabled;
    private String singleLandingZoneDir;

    private Logger log = LoggerFactory.getLogger(ControlFileProcessor.class);

    public List<LocalFileSystemLandingZone> getLandingZones() {
        List<LocalFileSystemLandingZone> landingZoneList;
        if (multipleLandingZonesEnabled) {
            landingZoneList = getMultipleLandingZones();
        } else {
            landingZoneList = new ArrayList<LocalFileSystemLandingZone>();
            landingZoneList.add(getSingleLandingZone());
        }
        return landingZoneList;
    }

    protected LocalFileSystemLandingZone getSingleLandingZone() {
        return new LocalFileSystemLandingZone(new File(singleLandingZoneDir));
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
            LogUtil.error(log, "Exception encountered extracting landing zones from tenant collection:", e);
        }
        return landingZoneList;
    }

    /**
     * @return the multipleLandingZonesEnabled
     */
    public boolean multipleLandingZonesEnabled() {
        return multipleLandingZonesEnabled;
    }

    /**
     * @param multipleLandingZonesEnabled the multipleLandingZonesEnabled to set
     */
    public void setMultipleLandingZonesEnabled(boolean multipleLandingZonesEnabled) {
        this.multipleLandingZonesEnabled = multipleLandingZonesEnabled;
    }

    /**
     * @return the singleLandingZoneDir
     */
    public String getSingleLandingZoneDir() {
        return singleLandingZoneDir;
    }

    /**
     * @param singleLandingZoneDir the singleLandingZoneDir to set
     */
    public void setSingleLandingZoneDir(String singleLandingZoneDir) {
        this.singleLandingZoneDir = singleLandingZoneDir;
    }
}
