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

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.PortalWSManager;
import org.slc.sli.dashboard.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.dashboard.web.controller.GenericLayoutController;

/**
 * Useful base for controller tests
 * @author agrebneva
 *
 */
public class ControllerTestBase {
    protected static Gson gson = new GsonBuilder().create();
    private static final String COMPONENT_ID = "abc01";
    private static final String CONFIG_STRING = "{id : " + COMPONENT_ID + ", type: \"LAYOUT\", data :{entity: \"student\",cacheKey: \"student\"}, items: [ {id : \"csi\", name: \"Student Info\", type: \"TAB\"}]}";
    private static final Config CONFIG = gson.fromJson(CONFIG_STRING, Config.class);
    private static final GenericEntity SIMPLE_GENERIC_ENTITY = new GenericEntity(new HashMap<String, Object>() {
        private static final long serialVersionUID = 1L;
        {
            put("id", COMPONENT_ID);
        }
    });

    protected MockHttpServletRequest request = new MockHttpServletRequest();

    // partially mock CustomizationAssemblyFactoryImpl
    protected CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {
        @Override
        protected Config getConfig(String componentId) {
            return CONFIG;
        }
        @Override
        public GenericEntity getDataComponent(String componentId, Object entityKey) {
            return SIMPLE_GENERIC_ENTITY;
        }

        @Override
        protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            return SIMPLE_GENERIC_ENTITY;
        }

        @Override
        public Collection<Config> getWidgetConfigs() {
            return Collections.emptyList();
        }
    };

    PortalWSManager portalWSManager = new PortalWSManager() {

        @Override
        public String getHeader(boolean isAdmin) {
            return "";
        }

        @Override
        public String getFooter(boolean isAdmin) {
            return "";
        }
    };

    protected <T> void setPortalWS(GenericLayoutController controller) {
        controller.setPortalWSManager(portalWSManager);
    }

    protected <T> void setCustomizationAssemblyFactory(GenericLayoutController controller) {
        controller.setCustomizedDataFactory(dataFactory);
    }

    protected <T> T loadFile(String fileName, Class<T> clazz) throws Exception {
        FileReader fr = new FileReader(new File(fileName));
        try {
            return new GsonBuilder().create().fromJson(fr, clazz);
        } finally {
            IOUtils.closeQuietly(fr);
        }
    }
}
