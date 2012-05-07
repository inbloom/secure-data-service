package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.tenant.TenantPopulator;
import org.slc.sli.ingestion.tenant.TenantRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests for TenantProcessor
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class TenantProcessorTest {

    @Autowired
    private TenantProcessor tenantProcessor;
    
    @InjectMocks
    @Autowired
    private TenantPopulator tenantPopulator;
    
    @Mock
    private TenantDA mockedTenantDA;

    @Before
    public void setup() {
        // Setup the mocked Mongo Template.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldProcessTenantRequest() throws Exception {
        
        //get a test tenantRecord
        TenantRecord tenantRecord = createTenantRecord();
        String tenantRecordJson = tenantRecord.toString();
        
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(tenantRecordJson);

        tenantProcessor.process(exchange);
        
        Mockito.verify(mockedTenantDA, Mockito.times(1)).insertTenant(Mockito.any(TenantRecord.class));
        //Mockito.verify(mockedTenantDA, Mockito.times(1)).insertTenant(Mockito.eq(tenantRecord));
        
        //check there is no error on the received message
        assertEquals("No error message should be set on exachange", null, exchange.getIn().getHeader("ErrorMessage"));
    }
    
    private TenantRecord createTenantRecord() throws IOException {
        InputStream tenantStream = EntityTestUtils.getResourceAsStream("tenants/testTenant.json");
        TenantRecord tenantRecord = TenantRecord.parse(tenantStream);
        return tenantRecord;
    }
}
