package org.slc.sli.unit.manager;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.manager.HistoricalViewManager;
import org.slc.sli.view.HistoricalDataResolver;
import org.slf4j.Logger;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

/**
 * Test class for HistoricalViewManager
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })

public class HistoricalViewManagerTest {
    private static Logger log = LoggerFactory.getLogger(HistoricalViewManagerTest.class);
    
    private HistoricalViewManager historicalViewManager; // class under test
    
    @Before
    public void setup() {
        HistoricalDataResolver historicalDataResolver = mock(HistoricalDataResolver.class);
        SortedSet<String> schoolYears = new TreeSet<String>();
        schoolYears.add("2009-2010");
        schoolYears.add("2010-2011");
        
        when(historicalDataResolver.getSchoolYears()).thenReturn(schoolYears);
        when(historicalDataResolver.getSubjectArea()).thenReturn("Test Subject Area");
        
        historicalViewManager = new HistoricalViewManager(historicalDataResolver);
    }
    
    @Test
    public void addHistoricalData() {
        ViewConfig testConfig = new ViewConfig();
        
        historicalViewManager.addHistoricalData(testConfig);
        assertEquals("There should have been two display sets added", 2, testConfig.getDisplaySet().size());
        assertEquals("Display name should have been set properly", "2009-2010",
                testConfig.getDisplaySet().get(0).getDisplayName());
        assertEquals("There should have been two fields added",
                2, testConfig.getDisplaySet().get(0).getField().size());
    }
    
}
