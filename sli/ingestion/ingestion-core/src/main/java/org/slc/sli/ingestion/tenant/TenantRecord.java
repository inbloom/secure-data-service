package org.slc.sli.ingestion.tenant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Container class for Tenant data entries
 * 
 * @author jtully
 */
public class TenantRecord {
    
    private List<LandingZoneRecord> landingZone;
    private String tenantId;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // mongoTemplate requires this constructor.
    public TenantRecord() {
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public List<LandingZoneRecord> getLandingZone() {
        return landingZone;
    }
    
    public void setLandingZone(List<LandingZoneRecord> landingZone) {
        this.landingZone = landingZone;
    }
    
    /**
     * Read in a TenantRecord object from a JSON InputStream
     * 
     * @param inputStream
     *            , JSON formatted InputStream
     * @return TenantRecord object
     * @throws IOException
     */
    public static TenantRecord parse(InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, TenantRecord.class);
    }
    
    /**
     * Read in a TenantRecord object from a JSON String
     * 
     * @param input
     *            , JSON formatted String
     * @return TenantRecord object
     * @throws IOException
     */
    public static TenantRecord parse(String input) throws IOException {
        return MAPPER.readValue(input, TenantRecord.class);
    }
    
    /**
     * Output the object as a JSON String
     */
    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        return toString().equals(o.toString());
    }
}
