package org.slc.sli.manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.modifier.ViewModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class ViewManagerTest {
    private static Logger log = LoggerFactory.getLogger(ViewManagerTest.class);
    
    private ViewManager viewManager; // class under test
    
    EntityManager entityManager;
    
    @Before
    public void setup() {
        entityManager = mock(EntityManager.class);
        viewManager = new ViewManager(new ArrayList<ViewConfig>());
        viewManager.setEntityManager(entityManager);
    }
    
    @Test
    public void testGetAndSetViewConfig() {
        List<ViewConfig> testConfigs = new ArrayList<ViewConfig>();
        ViewConfig config1 = new ViewConfig();
        ViewConfig config2 = new ViewConfig();

        testConfigs.add(config1);
        testConfigs.add(config2);

        viewManager.setViewConfigs(testConfigs);
        assertEquals(testConfigs, viewManager.getViewConfigs());
    }
    
    @Test 
    public void testSetActiveViewConfig() {
        ViewConfig config1 = new ViewConfig();
            
        viewManager.setActiveViewConfig(config1);
        assertEquals(config1, viewManager.getActiveConfig());
    }
    
    @Test 
    public void testApply() {
        ViewModifier modifier = mock(ViewModifier.class);

        viewManager.apply(modifier);
        verify(modifier).modify(any(ViewConfig.class));
    }
    
    @Test
    public void testGetApplicableViewConfigs() {
        ViewConfig threeThroughEight = new ViewConfig();
        threeThroughEight.setValue("3-8");
        
        ViewConfig nineThroughTwelve = new ViewConfig();
        nineThroughTwelve.setValue("9-12");

        ViewConfig kTo3 = new ViewConfig();
        kTo3.setValue("0-3");
        
        List<GenericEntity> studentsk3 = new ArrayList<GenericEntity>();
        List<GenericEntity> students3to8 = new ArrayList<GenericEntity>();
        List<GenericEntity> students9to12 = new ArrayList<GenericEntity>();
        
        List<String> emptyList = new ArrayList<String>();
        
        when(entityManager.getStudents("token", emptyList)).thenReturn(null);
        
        assertEquals("Should return empty list when student list is empty", 0,
                viewManager.getApplicableViewConfigs(emptyList, "token").size());
    }

}
