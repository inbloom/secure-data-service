package org.slc.sli.api.resources.security;

public interface TenantResource {
    
    LandingZoneInfo createLandingZone(String tenantId, String edOrgId);
    
    /**
     * Small data object to return multiple typesafe fields from createLandingZone
     * Package level visibility because this is just used between *Resource classes
     * in this package.
     * 
     * @author sashton
     * 
     */
    class LandingZoneInfo {
        
        private final String landingZonePath;
        private final String ingestionServerName;
        
        public LandingZoneInfo(String landingZonePath, String ingestionServerName) {
            super();
            this.landingZonePath = landingZonePath;
            this.ingestionServerName = ingestionServerName;
        }
        
        public String getLandingZonePath() {
            return landingZonePath;
        }
        
        public String getIngestionServerName() {
            return ingestionServerName;
        }
        
    }
}