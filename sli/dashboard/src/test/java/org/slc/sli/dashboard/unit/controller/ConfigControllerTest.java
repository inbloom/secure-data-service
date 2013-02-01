/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.unit.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Data;
import org.slc.sli.dashboard.entity.Config.Type;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.UserEdOrgManager;
import org.slc.sli.dashboard.manager.impl.PortalWSManagerImpl;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.web.controller.ConfigController;

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
            //No Op
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
        
        String response = configController.saveConfig(configMap);
        Assert.assertEquals("Success", response);
    }

    @Test
    public void testBadSave() throws Exception {
        Map<String, Config> mapOfConfigs = new HashMap<String, Config>();
        Config bad = new Config("+++", null, null, Type.FIELD, null, null, null, null, null);
        mapOfConfigs.put("something", bad);
        ConfigMap map = new ConfigMap();
        map.setConfig(mapOfConfigs);
        try {
            configController.saveConfig(map);
            Assert.fail("Should not be able to save");
        } catch (HttpMessageConversionException e) {
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
            public GenericEntity getUserCoursesAndSections(String token, Object key, Data config) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public GenericEntity getUserSectionList(String token, Object key, Data config) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public GenericEntity getSchoolInfo(String token, Object key, Data config) {
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
