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
package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * JUnit for student access to sections
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentToSectionValidatorTest {
    @Autowired
    private StudentToSectionValidator underTest;

    @Autowired
    private SecurityContextInjector injector;

    private Entity e = mock(Entity.class);

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        when(e.getEntityId()).thenReturn("riverTam");
        injector.setStudentContext(e);
        Map<String, Object> section1 = new HashMap<String, Object>();
        section1.put("_id", "quantumPhysics");
        Map<String, Object> section2 = new HashMap<String, Object>();
        section2.put("_id", "history");
        Map<String, Object> section3 = new HashMap<String, Object>();
        section3.put("_id", "dance");
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String,Object>>>();
        denormalized.put("section", Arrays.asList(section1, section2, section3));
        when(e.getDenormalizedData()).thenReturn(denormalized);
    }

    @Test
    public void test() {
        assertTrue(underTest.validate(EntityNames.SECTION, new HashSet<String>(Arrays.asList("quantumPhysics"))));
        assertTrue(underTest.validate(EntityNames.SECTION, new HashSet<String>(Arrays.asList("quantumPhysics", "history", "dance"))));
        assertFalse(underTest.validate(EntityNames.SECTION, new HashSet<String>(Arrays.asList("remedialMath"))));
        assertFalse(underTest.validate(EntityNames.SECTION, new HashSet<String>(Arrays.asList("remedialMath", "quantumPhysics"))));
    }

}
