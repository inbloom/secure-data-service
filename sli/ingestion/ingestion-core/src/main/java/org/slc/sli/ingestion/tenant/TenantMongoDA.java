package org.slc.sli.ingestion.tenant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Mongo implementation for access to tenant data.
 *
 * @author jtully
 */
@Component
public class TenantMongoDA implements TenantDA {

    public static final String TENANT_ID = "tenantId";
    public static final String INGESTION_SERVER = "ingestionServer";
    public static final String PATH = "path";
    public static final String LANDING_ZONE = "landingZone";
    public static final String TENANT_COLLECTION = "tenant";
    public static final String TENANT_TYPE = "tenant";
    public static final String EDUCATION_ORGANIZATION = "educationOrganization";
    public static final String DESC = "desc";

    private Repository<Entity> entityRepository;


    @Override
    public List<String> getLzPaths(String ingestionServer) {
        List<String> lzPaths = findTenantPathsByIngestionServer(ingestionServer);
        return lzPaths;
    }

    @Override
    public String getTenantId(String lzPath) {
        return findTenantIdByLzPath(lzPath);
    }

    @Override
    public void insertTenant(TenantRecord tenant) {
        if (entityRepository.findOne(TENANT_COLLECTION, new NeutralQuery(new NeutralCriteria(TENANT_ID, "=", tenant.getTenantId()))) == null) {
            entityRepository.create(TENANT_COLLECTION, getTenantBody(tenant));
        }
    }

    private Map<String, Object> getTenantBody(TenantRecord tenant) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(TENANT_ID, tenant.getTenantId());
        List<Map<String, String>> landingZones = new ArrayList<Map<String, String>>();
        if (tenant.getLandingZone() != null) {
            for (LandingZoneRecord landingZoneRecord : tenant.getLandingZone()) {
                Map<String, String> landingZone = new HashMap<String, String>();
                landingZone.put(EDUCATION_ORGANIZATION, landingZoneRecord.getEducationOrganization());
                landingZone.put(INGESTION_SERVER, landingZoneRecord.getIngestionServer());
                landingZone.put(PATH, landingZoneRecord.getPath());
                landingZone.put(DESC, landingZoneRecord.getDesc());
                landingZones.add(landingZone);
            }
        }
        body.put(LANDING_ZONE, landingZones);
        return body;
    }

    private List<String> findTenantPathsByIngestionServer(String targetIngestionServer) {
        List<String> tenantPaths = new ArrayList<String>();

        NeutralQuery query = new NeutralQuery(new NeutralCriteria("landingZone.ingestionServer", "=", targetIngestionServer));
        Iterable<Entity> entities = entityRepository.findAll(TENANT_COLLECTION , query);

        for (Entity entity : entities) {
            List<Map<String, String>> landingZones = (List<Map<String, String>>) entity.getBody().get(LANDING_ZONE);
            if (landingZones != null) {
                for (Map<String, String> landingZone : landingZones) {
                    String ingestionServer = landingZone.get(INGESTION_SERVER);
                    if (targetIngestionServer.equals(ingestionServer)) {
                        String path = landingZone.get(PATH);
                        if (path != null) {
                            tenantPaths.add(path);
                        }
                    }
                }
            }
        }
        return tenantPaths;
    }

    private String findTenantIdByLzPath(String lzPath) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("landingZone.path", "=", lzPath));
        Entity entity = entityRepository.findOne(TENANT_COLLECTION, query);
        return (String) entity.getBody().get(TENANT_ID);
    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }
}
