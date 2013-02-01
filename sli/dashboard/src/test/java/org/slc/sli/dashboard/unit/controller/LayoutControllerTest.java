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

import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.web.controller.LayoutController;

/**
 * Tesing layout controller
 *
 * @author svankina
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml", "/dashboard-servlet-test.xml" })
public class LayoutControllerTest extends ControllerTestBase  {
    LayoutController layoutController = new LayoutController() {
        @Override
        public boolean isAdmin() {
            return false;
        }
    };;

    @Before
    public void setup() throws Exception {
        setCustomizationAssemblyFactory(layoutController);
        setPortalWS(layoutController);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandle() throws Exception {
        ModelAndView mv = layoutController.handle("simpleLayout", null, request);
        Assert.assertEquals(2, ((Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS)).size());
        Assert.assertEquals(1, ((Map<String, GenericEntity>) mv.getModel().get(Constants.MM_KEY_DATA)).size());
        Assert.assertEquals(1, ((Collection<Config>) mv.getModel().get(Constants.MM_KEY_LAYOUT)).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandleWithId() throws Exception {
        ModelAndView mv = layoutController.handleWithId("simpleLayout", null, request);
        ModelAndView mv1 = layoutController.handle("simpleLayout", null, request);
        Map<String, Config> c = (Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS);
        Map<String, Config> c1 = (Map<String, Config>) mv1.getModel().get(Constants.MM_KEY_VIEW_CONFIGS);
        Assert.assertEquals(2, c.size());
        Assert.assertEquals(2, c1.size());
        for (Map.Entry<String, Config> conf : c.entrySet()) {
            Assert.assertEquals(conf.getValue().getName(), c1.get(conf.getKey()).getName());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandleLos() throws Exception {
        ModelAndView mv = layoutController.handleLos(request);
        Assert.assertEquals(2, ((Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS)).size());
    }
}
