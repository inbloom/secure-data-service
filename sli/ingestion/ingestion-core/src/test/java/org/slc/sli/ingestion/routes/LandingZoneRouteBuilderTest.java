package org.slc.sli.ingestion.routes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests for LandingZoneRouteBuilder
 * 
 * @author jtully
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class LandingZoneRouteBuilderTest {
    
    @Autowired
    private ControlFilePreProcessor ctrlFilePreProcessor;
    
    @Autowired 
    private ZipFileProcessor zipFileProcessor;
    
    @Test
    public void shouldCreateCtrlAndZipRoutes() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        
        String testPath = "testPath";
        List<String> testPaths = new ArrayList<String>();
        testPaths.add(testPath);
        
        RouteBuilder landingZoneRouteBuilder = new LandingZoneRouteBuilder(testPaths, 
                "seda:workItemQueue", zipFileProcessor, ctrlFilePreProcessor);
        
        camelContext.start();
        
        camelContext.addRoutes(landingZoneRouteBuilder);
        
        List<Route> routeList = camelContext.getRoutes();
        
        assertEquals("Number of routes found was not 2", 2, routeList.size());
        assertEquals("Ctrl route Id was not as expected", 
                LandingZoneRouteBuilder.CTRL_POLLER_PREFIX + testPath, routeList.get(0).getId());
        assertEquals("Zip route Id was not as expected", 
                LandingZoneRouteBuilder.ZIP_POLLER_PREFIX + testPath, routeList.get(1).getId());
        
        camelContext.stop();
    }
}
