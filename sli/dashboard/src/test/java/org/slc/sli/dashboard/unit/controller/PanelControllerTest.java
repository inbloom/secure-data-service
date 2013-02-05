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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Condition;
import org.slc.sli.dashboard.entity.Config.Data;
import org.slc.sli.dashboard.entity.Config.Item;
import org.slc.sli.dashboard.entity.Config.Type;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.ModelAndViewConfig;
import org.slc.sli.dashboard.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.dashboard.web.controller.PanelController;
import org.slc.sli.dashboard.web.entity.SafeUUID;

/**
 * Testing data controller
 *
 * @author ccheng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class PanelControllerTest {

    private final String componentId = "abc01";
    private final String rawId = "abcd-abcd-abcd-abcd-abcd";

    private MockHttpServletRequest request;
    private PanelController panelController;
    private SafeUUID id;

    // partially mock CustomizationAssemblyFactoryImpl
    CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {

        @Override
        public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey, boolean lazyOverride) {
            GenericEntity simpleEntity = new GenericEntity();
            simpleEntity.put("id", componentId);

            Type type = null;
            Condition condition = null;
            Item[] items = null;

            Map<String, Object> params = null;
            Data simpleData = new Data("entity", componentId, true, params);

            Config simpleViewConfig = new Config(componentId, "parent", "name", type, condition, simpleData, items,
                    "root", null);
            ModelAndViewConfig simpleModelAndViewConfig = new ModelAndViewConfig();

            simpleModelAndViewConfig.addData(componentId, simpleEntity);
            simpleModelAndViewConfig.addConfig(componentId, simpleViewConfig);

            return simpleModelAndViewConfig;
        }
    };

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        panelController = new PanelController();
        id = new SafeUUID(rawId);
    }

    @Test
    public void testHandleValidId() throws Exception {

        // expected result
        GenericEntity expectedEntity = new GenericEntity();
        expectedEntity.put("id", componentId);

        panelController.setCustomizedDataFactory(dataFactory);

//        GenericEntity res = panelController.handle(componentId, id, request);
//        Assert.assertEquals(expectedEntity, res.get("data"));

    }
}
