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

package org.slc.sli.api.resources.generic.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResourceServiceHelperTest {

    @Autowired
    private ResourceServiceHelper resourceServiceHelper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private Resource resource = null;
    private java.net.URI requestURI;

    private static final String URI = "http://some.net/api/generic/v1/students";
    private Resource ssaResource = null;
    private Resource sectionResource = null;

    @Before
    public void setup() throws Exception {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        resource = new Resource("v1", "students");
        ssaResource = new Resource("v1", "studentSectionAssociations");
        sectionResource = new Resource("v1", "sections");

        requestURI = new java.net.URI(URI);
    }

    @Test
    public void testAddTypeCriteria() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.TEACHERS);
        ApiQuery query = new ApiQuery();

        query = resourceServiceHelper.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 1, criteriaList.size());

        NeutralCriteria criteria = criteriaList.get(0);
        assertEquals("Should match", "type", criteria.getKey());
        assertEquals("Should match", NeutralCriteria.CRITERIA_IN, criteria.getOperator());
        assertEquals("Should match", Arrays.asList(def.getType()), criteria.getValue());
    }

    @Test
    public void testAddTypeCriteriaNoChange() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.STAFF);
        ApiQuery query = new ApiQuery();

        query = resourceServiceHelper.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }

    @Test
    public void testAddTypeCriteriaNullValues() {
        ApiQuery query = null;

        assertNull("Should be null", resourceServiceHelper.addTypeCriteria(null, null));

        query = new ApiQuery();
        query = resourceServiceHelper.addTypeCriteria(null, query);
        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }
}
