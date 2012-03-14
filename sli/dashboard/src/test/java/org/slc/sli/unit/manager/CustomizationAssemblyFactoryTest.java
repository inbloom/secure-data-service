package org.slc.sli.unit.manager;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;

/**
 * Test CustomizationAssemblyFactory features
 * @author agrebneva
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class CustomizationAssemblyFactoryTest {
    @Autowired
    ApplicationContext applicationContext;

    private static final String DEFAULT_LAYOUT_JSON = 
            "{id : 'studentProfile', type: 'LAYOUT', " 
          + " data :{entity: 'mock', alias: 'mock' }, " 
          + " items: [{id : 'csi', name: 'Student Info', type: 'PANEL'},"
          + "         {id: 'tab2', name: 'Attendance and Discipline', type : 'TAB', " 
          + "          items: [{id : 'csi', type: 'PANEL'}]}]}";
    private static final String DEFAULT_LAYOUT_TOO_DEEP_JSON = 
            "{id : 'deep', type: 'LAYOUT', " 
          + " data :{entity: 'mock', alias: 'mock' }, " 
          + " items: [{id : 'deep', name: 'Student Info', type: 'PANEL'}]}";
    private static final String DEFAULT_CSI_JSON = 
            "{id : 'csi', type: 'PANEL', data :{entity: 'mock',alias: 'mock' }}";
    private Map<String, Config> configMap;
    
    private CustomizationAssemblyFactoryImpl customizationAssemblyFactory = new CustomizationAssemblyFactoryImpl() {
        
        protected Config getConfig(String componentId) {
            
            return configMap.get(componentId);
        }
        protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            return null;
        }
    };
    
    @Before
    public void setupAll() {
        Gson gson = new GsonBuilder().create();
        customizationAssemblyFactory.setApplicationContext(applicationContext);
        configMap = new HashMap<String, Config>();
        configMap.put("studentProfile", gson.fromJson(DEFAULT_LAYOUT_JSON, Config.class));
        configMap.put("csi", gson.fromJson(DEFAULT_CSI_JSON, Config.class));
        
        configMap.put("deep", gson.fromJson(DEFAULT_LAYOUT_TOO_DEEP_JSON, Config.class));
        customizationAssemblyFactory.setApplicationContext(applicationContext);
    }
    
    /**
     * Test entity reference map contains student mapping
     */
    @Test
    public void testEntityReferenceMapNoEmpty() {
        Assert.assertTrue("Student entity reference must exists", customizationAssemblyFactory.hasCachedEntityMapperReference("student"));
    }
    
    /**
     * Test simple layout contains expected number of configs
     */
    @Test
    public void testSimpleLayout() {
        ModelAndViewConfig viewAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig("studentProfile", 1);
        
        Assert.assertEquals(3, viewAndConfig.getComponentViewConfigMap().size());
    }
    
    /**
     * Test not to allow infinite recursion
     */
    @Test
    public void testConfigTooDeep() {
        try {
            customizationAssemblyFactory.getModelAndViewConfig("deep", 1);
        Assert.fail("Should not allow deep config structures");
        } catch (Throwable t) {
            Assert.assertEquals("The items hierarchy is too deep - only allow 3 elements", t.getMessage());
        }
    }
}
