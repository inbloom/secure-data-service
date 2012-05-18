package org.slc.sli.unit.controller;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.web.controller.ConfigController;

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
        @SuppressWarnings("unchecked")
        Map<String, Config> mapOfConfigs =
                loadFile(Config.class.getClassLoader().getResource(CONFIG_MAP_LOCATION).getFile(), Map.class);
        ConfigMap map = new ConfigMap();
        map.setConfig(mapOfConfigs);
        try {
            String response = configController.saveConfig(map);
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
}