package org.slc.sli.unit.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Data;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.manager.impl.PortalWSManagerImpl;
import org.slc.sli.util.Constants;
import org.slc.sli.web.controller.ConfigController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

/**
 * Testing config controller
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml", "/dashboard-servlet-test.xml" })
public class ConfigControllerTest extends ControllerTestBase {
    
    private ConfigController configController = new ConfigController() {
        @Override
        public void putCustomConfig(ConfigMap map) {
            
        }
    };
    
    private static final String CONFIG_MAP_LOCATION = "custom/IL-DAYBREAK/customConfig.json";
    
    @Before
    public void setup() throws Exception {
        setCustomizationAssemblyFactory(configController);
    }
    
    @Test
    public void testSave() throws Exception {
        ConfigMap configMap = loadFile(Config.class.getClassLoader().getResource(CONFIG_MAP_LOCATION).getFile(),
                ConfigMap.class);
        try {
            String response = configController.saveConfig(configMap);
            Assert.assertEquals("Success", response);
        } catch (Exception e) {
            Assert.fail("Should pass validation but getting " + e.getMessage());
        }
    }
    
    @Test
    public void testBadSave() throws Exception {
        Map<String, Config> mapOfConfigs = new HashMap<String, Config>();
        Config bad = new Config("+++", null, null, Type.FIELD, null, null, null, null);
        mapOfConfigs.put("something", bad);
        ConfigMap map = new ConfigMap();
        map.setConfig(mapOfConfigs);
        try {
            configController.saveConfig(map);
            Assert.fail("Should not be able to save");
        } catch (Exception e) {
            Assert.assertEquals("Invalid input parameter configMap", e.getMessage().substring(0, 33));
        }
    }
    
    @Test
    public void testGetConfig() throws Exception {
        configController.setUserEdOrgManager(new UserEdOrgManager() {
            
            @Override
            public GenericEntity getUserInstHierarchy(String token, Object key, Data config) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public List<GenericEntity> getUserInstHierarchy(String token) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public EdOrgKey getUserEdOrg(String token) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public GenericEntity getStaffInfo(String token) {
                GenericEntity entity = new GenericEntity();
                GenericEntity edOrg = new GenericEntity();
                List<String> list = new ArrayList<String>();
                list.add(Constants.LOCAL_EDUCATION_AGENCY);
                edOrg.put(Constants.ATTR_ORG_CATEGORIES, list);
                entity.put(Constants.ATTR_ED_ORG, edOrg);
                return entity;
            }
        });
        configController.setPortalWSManager(new PortalWSManagerImpl() {
            @Override
            public String getHeader(boolean isAdmin) {
                return null;
            }
        });
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelAndView model = null;
        try {
            model = configController.getConfig(request);
        } catch (IllegalAccessException e) {
            // this exception is thrown because not admin
            model = null;
        }
        Assert.assertNull(model);
    }
}