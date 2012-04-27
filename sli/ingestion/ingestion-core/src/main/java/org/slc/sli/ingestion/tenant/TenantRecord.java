package org.slc.sli.ingestion.tenant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Container class for Tenant data entries
 * 
 * @author jtully
 */
public class TenantRecord {

    private Map<String, Object> geographicLocation;
    private List<LandingZoneRecord> landingZone;
    private String tenantId;

    // mongoTemplate requires this constructor.
    public TenantRecord() { }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public Map<String, Object> getGeographicLocation() {
        return geographicLocation;
    }
    
    public void setGeographicLocation(Map<String, Object> geographicLocation) {
        this.geographicLocation = geographicLocation;
    }
    
    public List<LandingZoneRecord> getLandingZone() {
        return landingZone;
    }
    
    public void setLandingZone(List<LandingZoneRecord> landingZone) {
        this.landingZone = landingZone;
    }
    
    public static TenantRecord parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, TenantRecord.class);
    }

}
