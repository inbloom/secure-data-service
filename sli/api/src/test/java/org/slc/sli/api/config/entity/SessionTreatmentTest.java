/*
 *
 *  * Copyright 2013 inBloom, Inc. and its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.slc.sli.api.config.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.DefinitionFactory;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;


/**
 * Basic unit tests for SessionTreatment
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class SessionTreatmentTest {

    //class being tested
    @Autowired
    private SessionTreatment sessionTreatment;

    @Autowired
    private DefinitionFactory factory;

    private EntityDefinition sessionEntityDefinition;

    private EntityBody sessionWithEdOrg;
    private EntityBody sessionWithSchoolId;
    private EntityBody sessionWithBoth;

    private static final String edOrgId = "0987654321";
    private static final String schoolId = "0987654322";


    @Before
    public void setUp(){
        sessionEntityDefinition = factory.makeEntity(EntityNames.SESSION).withTreatments(sessionTreatment).build();

        sessionWithEdOrg = new EntityBody();
        sessionWithEdOrg.put("id", "1234567890");
        sessionWithEdOrg.put("schoolYear", "2001-2002");
        sessionWithEdOrg.put("sessionName", "Fall 2001 Fake Session");
        sessionWithEdOrg.put("term", "Fake Semester");
        sessionWithEdOrg.put("educationOrganizationReference", edOrgId);
        sessionWithEdOrg.put("entityType", "session");
        sessionWithEdOrg.put("totalInstructionalDays", "75");
        sessionWithEdOrg.put("beginDate", "2001-09-01");

        sessionWithSchoolId = new EntityBody();
        sessionWithSchoolId.put("id", "1234567891");
        sessionWithSchoolId.put("schoolYear", "2002-2003");
        sessionWithSchoolId.put("sessionName", "Fall 2002 Fake Session");
        sessionWithSchoolId.put("term", "Fake Semester");
        sessionWithSchoolId.put("schoolId", schoolId);
        sessionWithSchoolId.put("entityType", "session");
        sessionWithSchoolId.put("totalInstructionalDays", "75");
        sessionWithSchoolId.put("beginDate", "2002-09-01");

        sessionWithBoth = new EntityBody();
        sessionWithBoth.put("id", "1234567892");
        sessionWithBoth.put("schoolYear", "2003-2004");
        sessionWithBoth.put("sessionName", "Fall 2003 Fake Session");
        sessionWithBoth.put("term", "Fake Semester");
        sessionWithBoth.put("educationOrganizationReference", edOrgId);
        sessionWithBoth.put("schoolId", schoolId);
        sessionWithBoth.put("entityType", "session");
        sessionWithBoth.put("totalInstructionalDays", "75");
        sessionWithBoth.put("beginDate", "2003-09-01");
    }

    @Test
    public void testToStored(){
        EntityBody toStore = sessionTreatment.toStored(sessionWithEdOrg, sessionEntityDefinition);
        assertEquals("Output value of educationOrganizationReference did not match the input value", toStore.get("educationOrganizationReference"), edOrgId);
        assertFalse("Output session entity has a schoolId attribute.", toStore.containsKey("schoolId"));

        toStore = sessionTreatment.toStored(sessionWithSchoolId, sessionEntityDefinition);
        assertFalse("Output session entity has a schoolId attribute.", toStore.containsKey("schoolId"));
        assertEquals("Output value of educationOrganizationReference did not match the schoolId input", toStore.get("educationOrganizationReference"), schoolId);

        toStore = sessionTreatment.toStored(sessionWithBoth, sessionEntityDefinition);
        assertFalse("Output session entity has a schoolId attribute.", toStore.containsKey("schoolId"));
        assertEquals("Output value of educationOrganizationReference did not match the input value", toStore.get("educationOrganizationReference"), edOrgId);

    }

    @Test
    public void testToExposed(){
        EntityBody toExpose = sessionTreatment.toExposed(sessionWithEdOrg, sessionEntityDefinition, null);
        assertEquals("Output value of educationOrganizationReference did not match the input value", toExpose.get("educationOrganizationReference"), edOrgId);
        assertEquals("Output value of schoolId did not match the educationOrganizationReference value", toExpose.get("schoolId"), edOrgId);

        toExpose = sessionTreatment.toExposed(sessionWithSchoolId, sessionEntityDefinition, null);
        assertEquals("Output value of educationOrganizationReference did not match the schoolId value", toExpose.get("educationOrganizationReference"), schoolId);
        assertEquals("Output value of schoolId did not match the input value", toExpose.get("schoolId"), schoolId);

        toExpose = sessionTreatment.toExposed(sessionWithBoth, sessionEntityDefinition, null);
        assertEquals("Output value of educationOrganizationReference did not match the input value", toExpose.get("educationOrganizationReference"), edOrgId);
        assertEquals("Output value of schoolId did not match the educationOrganizationReference value", toExpose.get("schoolId"), edOrgId);
    }

}
