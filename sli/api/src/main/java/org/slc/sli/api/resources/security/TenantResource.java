package org.slc.sli.api.resources.security;

import javax.ws.rs.core.Response;

/**
 * Provides a method to create landing zones
 * 
 * @author sashton
 * 
 */
public interface TenantResource {
    
    LandingZoneInfo createLandingZone(String tenantId, String edOrgId) throws TenantResourceCreationException;
    
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
    
    /**
     * Used to communicate problems encountered during TenantResource creation.
     * The message indicates the specific issue.
     * 
     * @author srichards
     * 
     */
    public class TenantResourceCreationException extends Exception {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        private Response.Status status;
        
        /**
         * Constructs a new exception with the specified detail message. The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         * 
         * @param message
         *            the detail message. The detail message is saved for
         *            later retrieval by the {@link #getMessage()} method.
         */
        public TenantResourceCreationException(Response.Status s, String message) {
            super(message);
            status = s;
        }
        
        public Response.Status getStatus() {
            return status;
        }
    }
}