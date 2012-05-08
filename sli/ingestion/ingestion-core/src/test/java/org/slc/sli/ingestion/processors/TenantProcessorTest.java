package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
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

    @InjectMocks
    @Autowired
    private TenantProcessor tenantProcessor;
    
    @Mock
    private CamelContext mockedCamelContext;
    
    @Mock
    private TenantDA mockedTenantDA;

    @Before
    public void setup() {
        // Setup the mocked Mongo Template.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldProcessTenantRequest() throws Exception {
        
        List<String> testLzPaths = new ArrayList<String>();
        testLzPaths.add("testLz");
        
        List<Route> routes = new ArrayList<Route>();
        
        when(mockedTenantDA.getLzPaths(Mockito.any(String.class))).thenReturn(testLzPaths);
        when(mockedCamelContext.getRoutes()).thenReturn(routes);
        
        //get a test tenantRecord
        
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        tenantProcessor.process(exchange);
        
        Mockito.verify(mockedCamelContext, Mockito.times(0)).stopRoute(Mockito.any(String.class));
        Mockito.verify(mockedCamelContext, Mockito.times(1)).addRoutes(Mockito.any(RouteBuilder.class));
        
        //check there is no error on the received message
        assertEquals("Header on exchange should indicate success", TenantProcessor.TENANT_POLL_SUCCESS, exchange.getIn().getHeader(TenantProcessor.TENANT_POLL_HEADER));
    }
}
