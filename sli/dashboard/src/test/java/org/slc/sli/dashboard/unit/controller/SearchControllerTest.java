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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dashboard.web.controller.SearchController;
import org.slc.sli.dashboard.web.entity.StudentSearch;

/**
 * search test
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml", "/dashboard-servlet-test.xml" })
public class SearchControllerTest extends ControllerTestBase  {
    SearchController searchController = new SearchController() {
        @Override
        public boolean isAdmin() {
            return false;
        }
    };

    @Before
    public void setup() throws Exception {
        setCustomizationAssemblyFactory(searchController);
        setPortalWS(searchController);
    }

    @Test
    public void testHandle() throws Exception {
        Assert.assertNotNull(searchController.handle(new StudentSearch("M"), request));
    }
}
