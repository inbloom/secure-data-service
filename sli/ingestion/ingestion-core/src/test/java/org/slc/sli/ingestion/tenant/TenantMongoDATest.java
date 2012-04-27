package org.slc.sli.ingestion.tenant;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for testing the TenantMongoDA class.
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TenantMongoDATest {

    @Autowired
    private TenantMongoDA tenantDA;

    private MongoTemplate mockedMongoTemplate;
    
    private TenantRecord tenantRecord;
    
    private final String ingestionServerName = "ingestion_server_host";
    private final String lzPath1 = "lz_path_1";
    private final String lzPath2 = "lz_path_2";
    private final String tenantId = "test_tenant_id";
    
    @Before
    public void setup() {
        // Setup the mocked Mongo Template.
        mockedMongoTemplate = mock(MongoTemplate.class);
        tenantDA.setTenantMongoTemplate(mockedMongoTemplate);
        
        createTestTenantRecord();
    }
    
    @Test
    public void shouldGetLzPathsFromIngestionServerName() {
        List<TenantRecord> testTenantRecords = new ArrayList<TenantRecord>();
        testTenantRecords.add(tenantRecord);
        
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(TenantRecord.class),
                Mockito.eq("tenant"))).thenReturn(testTenantRecords);
        
        List<String> lzPathsResult = tenantDA.getLzPaths(ingestionServerName);
        
        assertNotNull("lzPathsResult was null", lzPathsResult);
        assertEquals("lzPathsResult was not match expected size", 2, lzPathsResult.size());
        assertEquals("lzPathsResult[0] did not match expected value", lzPath1, lzPathsResult.get(0));
        assertEquals("lzPathsResult[1] did not match expected value", lzPath2, lzPathsResult.get(1));
        
        Mockito.verify(mockedMongoTemplate, Mockito.times(1)).find(Mockito.any(Query.class), 
                Mockito.eq(TenantRecord.class), Mockito.eq("tenant"));
    }
    
    @Test
    public void shouldGetTenantIdFromLzPath() {
        
        List<TenantRecord> testTenantRecords = new ArrayList<TenantRecord>();
        testTenantRecords.add(tenantRecord);
        
        when(mockedMongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(TenantRecord.class),
                Mockito.eq("tenant"))).thenReturn(tenantRecord);
        
        String tenantIdResult = tenantDA.getTenantId(lzPath1);
        
        assertNotNull("tenantIdResult was null", tenantIdResult);
        assertEquals("tenantIdResult did not match expected value", tenantId, tenantIdResult);
        
        Mockito.verify(mockedMongoTemplate, Mockito.times(1)).findOne(Mockito.any(Query.class), 
                Mockito.eq(TenantRecord.class), Mockito.eq("tenant"));
    }
    
    private void createTestTenantRecord() {
        List<LandingZoneRecord> lzList = new ArrayList<LandingZoneRecord>();
        
        LandingZoneRecord lz1 = new LandingZoneRecord();
        lz1.setIngestionServer(ingestionServerName);
        lz1.setPath(lzPath1);
        
        LandingZoneRecord lz2 = new LandingZoneRecord();
        lz2.setIngestionServer(ingestionServerName);
        lz2.setPath(lzPath2);
        
        lzList.add(lz1);
        lzList.add(lz2);
        
        tenantRecord = new TenantRecord();
        tenantRecord.setLandingZone(lzList);
        tenantRecord.setTenantId(tenantId);
    }
}
