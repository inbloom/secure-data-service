package org.slc.sli.ingestion.tenant;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Mongo implementation for access to tenant data.
 * 
 * @author jtully
 */
@Component
public class TenantMongoDA implements TenantDA {

    private static MongoTemplate tenantMongoTemplate;
    private static final String TENANT_COLLECTION = "tenant";
    
    @Override
    public List<String> getLzPaths(String ingestionServer) {
        List<String> lzPaths = new ArrayList<String>();
        List<TenantRecord> tenants = findTenantByIngestionServer(ingestionServer);
        for (TenantRecord tenant : tenants) {
            List<LandingZoneRecord> landingZones = (List<LandingZoneRecord>) tenant.getLandingZone();
            for (LandingZoneRecord lz : landingZones) {
                String serverVal = lz.getIngestionServer();
                if (serverVal != null && serverVal.equals(ingestionServer)) {
                    lzPaths.add(lz.getPath());
                }
            }
        }
        return lzPaths;
    }

    @Override
    public String getTenantId(String lzPath) {
        
        String tenantId = null;
        TenantRecord tenant = findTenantByLzPath(lzPath);
        if (tenant != null) {
            tenantId = tenant.getTenantId();
        }
        return tenantId;
    }
    
    @Override
    public void dropTenants() {
        //TODO we could rename here instead to avoid losing the old tenant collection
        tenantMongoTemplate.dropCollection(TENANT_COLLECTION);
    }
    
    @Override
    public void insertTenant(TenantRecord tenant) {
        tenantMongoTemplate.insert(tenant, TENANT_COLLECTION);
    }
    
    
    public MongoTemplate getTenantMongoTemplate() {
        return tenantMongoTemplate;
    }

    @Resource
    public void setTenantMongoTemplate(MongoTemplate mongoTemplate) {
        tenantMongoTemplate = mongoTemplate;
    }
    
    private List<TenantRecord> findTenantByIngestionServer(String ingestionServer) {
        Query query = new Query(Criteria.where("landingZone.ingestionServer").is(ingestionServer));
        return tenantMongoTemplate.find(query, TenantRecord.class, TENANT_COLLECTION);
    }
    
    private TenantRecord findTenantByLzPath(String lzPath) {
        Query query = new Query(Criteria.where("landingZone.path").is(lzPath));
        return tenantMongoTemplate.findOne(query, TenantRecord.class, TENANT_COLLECTION);
    }
    
}
