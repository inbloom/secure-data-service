/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.security;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.enums.Right;

/**
 *
 * @author dkornishev
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SchemaDataProviderTest {

    private static final String RESTRICTED_FIELD = "economicDisadvantaged";

    private static final String ENTITY_TYPE      = "student";

    @Resource
    private SchemaDataProvider  provider;

    @Test
    public void testGeneral() {
        Assert.assertTrue(provider.getRequiredReadLevels(ENTITY_TYPE, "studentUniqueStateId").contains(Right.READ_GENERAL));
        Assert.assertTrue(provider.getRequiredWriteLevels(ENTITY_TYPE, "studentUniqueStateId").contains(Right.WRITE_GENERAL));
    }

    @Test
    public void testRestricted() {
        Assert.assertTrue(provider.getRequiredReadLevels(ENTITY_TYPE, RESTRICTED_FIELD).contains(Right.READ_RESTRICTED));
        Assert.assertTrue(provider.getRequiredWriteLevels(ENTITY_TYPE, RESTRICTED_FIELD).contains(Right.WRITE_RESTRICTED));
    }

    @Test
    public void testDeepTraversal() {
        Assert.assertTrue(provider.getRequiredReadLevels(ENTITY_TYPE, "name.firstName").contains(Right.READ_GENERAL));
    }

    @Test
    public void testSphere() {
        Assert.assertEquals("CDM", this.provider.getDataSphere(ENTITY_TYPE));
        Assert.assertEquals("Admin", this.provider.getDataSphere("realm"));
    }
}
