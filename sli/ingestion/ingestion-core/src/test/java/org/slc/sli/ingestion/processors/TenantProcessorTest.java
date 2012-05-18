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
import org.slc.sli.ingestion.routes.LandingZoneRouteBuilder;
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
    
    @Mock
    private Route mockedZipRoute;
    
    @Mock
    private Route mockedCtrlRoute;

    @Before
    public void setup() {
        // Setup the mocked Mongo Template.
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to check that a single route that is added to the
     * tenant collection is added by processor.
     * @throws Exception
     */
    @Test
    public void shouldAddNewLz() throws Exception {
        
        List<String> testLzPaths = new ArrayList<String>();
        testLzPaths.add("."); //this must be a path that exists on all platforms
        
        List<Route> routes = new ArrayList<Route>();
        
        when(mockedTenantDA.getLzPaths(Mockito.any(String.class))).thenReturn(testLzPaths);
        when(mockedCamelContext.getRoutes()).thenReturn(routes);
        
        //get a test tenantRecord
        
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        tenantProcessor.process(exchange);
        
        Mockito.verify(mockedCamelContext, Mockito.times(0)).stopRoute(Mockito.any(String.class));
        Mockito.verify(mockedCamelContext, Mockito.times(1)).addRoutes(Mockito.any(RouteBuilder.class));
        
        //check there is no error on the received message
        assertEquals("Header on exchange should indicate success", 
                TenantProcessor.TENANT_POLL_SUCCESS, exchange.getIn().getHeader(TenantProcessor.TENANT_POLL_HEADER));
    }
    
    /**
     * Test to check that a single route that does not exist
     * in the tenant DB collection is removed by processor
     * @throws Exception
     */
    @Test
    public void shouldRemoveOldLz() throws Exception {
        
        List<String> testLzPaths = new ArrayList<String>();
        final String oldCtrlRouteId = LandingZoneRouteBuilder.CTRL_POLLER_PREFIX + "oldRouteId";
        final String oldZipRouteId = LandingZoneRouteBuilder.ZIP_POLLER_PREFIX + "oldRouteId";
        
        // create a test route with a RouteBuilder
        // is there a simpler way of getting a test route??
        /*
        CamelContext testCamelContext = new DefaultCamelContext();
        RouteBuilder routeBuilder = new RouteBuilder() {    
            @Override
            public void configure() throws Exception {
                from("seda:testIn")
                .routeId(oldRouteId)
                .to("seda:testOut");
            }
        };
        testCamelContext.start();
        testCamelContext.addRoutes(routeBuilder);
        
        Route testRoute = testCamelContext.getRoute(oldRouteId);
        List<Route> testRouteList = new ArrayList<Route>();
        testRouteList.add(testRoute);
        */
        
        List<Route> testRouteList = new ArrayList<Route>();
        testRouteList.add(mockedCtrlRoute);
        testRouteList.add(mockedZipRoute);
        when(mockedCtrlRoute.getId()).thenReturn(oldCtrlRouteId);
        when(mockedZipRoute.getId()).thenReturn(oldZipRouteId);
        when(mockedTenantDA.getLzPaths(Mockito.any(String.class))).thenReturn(testLzPaths);
        when(mockedCamelContext.getRoutes()).thenReturn(testRouteList);
        
        //get a test tenantRecord
        Exchange exchange = new DefaultExchange(mockedCamelContext);

        tenantProcessor.process(exchange);
        
        Mockito.verify(mockedCamelContext, Mockito.times(1)).stopRoute(Mockito.eq(oldCtrlRouteId));
        Mockito.verify(mockedCamelContext, Mockito.times(1)).stopRoute(Mockito.eq(oldZipRouteId));
        Mockito.verify(mockedCamelContext, Mockito.times(0)).addRoutes(Mockito.any(RouteBuilder.class));
        
        //check there is no error on the received message
        assertEquals("Header on exchange should indicate success", 
                TenantProcessor.TENANT_POLL_SUCCESS, exchange.getIn().getHeader(TenantProcessor.TENANT_POLL_HEADER));
    }
    
}
