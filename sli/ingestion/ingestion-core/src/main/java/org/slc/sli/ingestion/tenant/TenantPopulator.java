package org.slc.sli.ingestion.tenant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slc.sli.ingestion.routes.IngestionRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Populates the tenant database collection with default tenant collections.
 * 
 * @author jtully
 */
public class TenantPopulator implements ResourceLoaderAware {
        
    Logger log = LoggerFactory.getLogger(IngestionRouteBuilder.class);
    
    private ResourceLoader resourceLoader;
    
    private String parentLandingZoneDir;
    
    private List<String> tenantRecordResourcePaths;
    
    private static final String HOSTNAME_PLACEHOLDER = "<hostname>";
    private static final String PARENT_LZ_PATH_PLACEHOLDER = "<lzpath>";
    
    /**
     * Populate the tenant data store with a default set of tenants.
     * 
     */
    public void populateDefaultTenants() {
        try {
            TenantDA tenantDA = new TenantMongoDA();
            String hostName = InetAddress.getLocalHost().getHostName();
            List<TenantRecord> tenants = constructTenantCollection(hostName);
            tenantDA.dropTenants();
            for (TenantRecord tenant : tenants) {
                tenantDA.insertTenant(tenant);
            }
        } catch (Exception e) {
            log.error("Exception:", e);
        }
    }
    
    /**
     * Construct the tenant collection based on the configured TenantRecord resources.
     * 
     * @param hostName
     * @return a list of constructed TenantRecord objects
     */
    private List<TenantRecord> constructTenantCollection(String hostname) {
        List<TenantRecord> tenants = new ArrayList<TenantRecord>();
        for (String tenantResourcePath : tenantRecordResourcePaths) {
            TenantRecord tenant = loadTenant(tenantResourcePath);
            replaceTenantPlaceholderFields(tenant, hostname);
            createTenantLzDirectory(tenant);
            tenants.add(tenant);
        }
        return tenants;
    }
    
    /**
     * 
     * Create the landing zone directory for a tenant.
     * 
     * @param tenant, the tenant for which to create landing zone directories
     * 
     */
    private void createTenantLzDirectory(TenantRecord tenant) {
        List<LandingZoneRecord> landingZones = tenant.getLandingZone();
        for (LandingZoneRecord lz : landingZones) {
            String lzPath = lz.getPath();
            File lzDirectory = new File(lzPath);
            lzDirectory.mkdir();
        } 
    }
    
    /**
     * 
     * Process TenantRecord, Replacing hostname and lzPath placeholder fields with
     * the actual values.
     * 
     * @param tenant record to be processed
     * @param hostname the hostname to be used in placeholder replacement
     */
    private void replaceTenantPlaceholderFields(TenantRecord tenant, String hostname) {
        List<LandingZoneRecord> landingZones = tenant.getLandingZone();
        for (LandingZoneRecord lz : landingZones) {
            //replace hostname field
            String serverVal = lz.getIngestionServer();
            serverVal = serverVal.replaceFirst(HOSTNAME_PLACEHOLDER, hostname);
            lz.setIngestionServer(serverVal);
            
            //replace pathname field
            String pathVal = lz.getPath();
            pathVal = pathVal.replaceFirst(PARENT_LZ_PATH_PLACEHOLDER, parentLandingZoneDir);
            lz.setPath(pathVal);
        }
    }
    
    /**
     * Loads a TenantRecord from a tenant resource
     * @param resourcePath to load into a tenant record
     * @return the loaded tenant
     */
    private TenantRecord loadTenant(String resourcePath) {
        TenantRecord tenant = null;
        InputStream tenantIs = null;
        try {
            Resource config = resourceLoader.getResource(resourcePath);

            if (config.exists()) {
                tenantIs = config.getInputStream();
                tenant = TenantRecord.parse(tenantIs);
            }
        } catch (IOException e) {
            log.error("Exception encountered loading tenant resource: ", e);
        } finally {
            IOUtils.closeQuietly(tenantIs);
        }
        return tenant;
    }
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * @return the parentLandingZoneDir
     */
    public String getParentLandingZoneDir() {
        return parentLandingZoneDir;
    }

    /**
     * @param parentLandingZoneDir the parentLandingZoneDir to set
     */
    public void setParentLandingZoneDir(String singleLandingZoneDir) {
        this.parentLandingZoneDir = singleLandingZoneDir;
    }
    
    public List<String> getTenantRecordResourcePaths() {
        return tenantRecordResourcePaths;
    }
    
    public void setTenantRecordResourcePaths(List<String> tenantRecordResourcePaths) {
        this.tenantRecordResourcePaths = tenantRecordResourcePaths;
    }

}
