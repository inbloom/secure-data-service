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

package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Utility class for constructing ed-org hierarchies for use in test classes.
 *
 *
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EdOrgHelperTest {

    @Autowired
    EdOrgHelper helper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    /*
     *  Create an EdOrg Hierarchy that looks like
     *  sea1 --> staff4
     *   | \
     *   |  lea4
     *  lea1 --> staff1
     *   |  \
     *   |   school1 --> teacher1
     *  lea2 --> staff2
     *   |  \
     *   |   school2 --> teacher1
     *  lea3 --> staff2
     *   |  \
     *   |   school3 --> teacher3
     */

    Entity staff1 = null;   //directly associated with lea1
    Entity staff2 = null;   //directly associated with lea2
    Entity staff3 = null;   //directly associated with lea3
    Entity staff4 = null;   //directly associated with sea1
    Entity sea1 = null;
    Entity lea1 = null;
    Entity lea2 = null;
    Entity lea3 = null;
    Entity lea4 = null;
    Entity school1 = null;
    Entity school2 = null;
    Entity school3 = null;
    Entity teacher1 = null;
    Entity teacher2 = null;
    Entity teacher3 = null;

    @Before
    public void setup() {

        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff3");
        staff3 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff4");
        staff4 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        teacher1 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        teacher2 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        teacher3 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        sea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", sea1.getEntityId());
        lea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        lea2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", lea2.getEntityId());
        lea3 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", sea1.getEntityId());
        lea4 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea2.getEntityId());
        school2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea3.getEntityId());
        school3 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea2.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea3.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", sea1.getEntityId());
        body.put("staffReference", staff4.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school1.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher1.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school2.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher2.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school3.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher3.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);
    }


    @Test
    public void testStaff1() {
        List<String> leas = helper.getDistricts(staff1);
        assertTrue("staff1 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff1 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(staff1);
        assertTrue("staff1 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testStaff2() {
        List<String> leas = helper.getDistricts(staff2);
        assertTrue("staff2 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff2 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(staff2);
        assertTrue("staff2 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testStaff3() {
        List<String> leas = helper.getDistricts(staff3);
        assertTrue("staff3 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff3 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(staff3);
        assertTrue("staff3 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testStaff4() {
        List<String> leas = helper.getDistricts(staff4);
        assertTrue("staff4 must see lea1", leas.contains(lea1.getEntityId()));
        assertTrue("staff4 must lea4", leas.contains(lea4.getEntityId()));
        assertEquals("staff4 must only see two districts", 2, leas.size());

        List<String> seas = helper.getSEAs(staff4);
        assertTrue("staff4 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testTeacher1() {
        List<String> leas = helper.getDistricts(teacher1);
        assertTrue("teacher1 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher1 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(teacher1);
        assertTrue("teacher1 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testTeacher2() {
        List<String> leas = helper.getDistricts(teacher2);
        assertTrue("teacher2 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher2 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(teacher2);
        assertTrue("teacher2 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testTeacher3() {
        List<String> leas = helper.getDistricts(teacher3);
        assertTrue("teacher3 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher3 must only see one district", 1, leas.size());

        List<String> seas = helper.getSEAs(teacher3);
        assertTrue("teacher3 must see sea1", seas.contains(sea1.getEntityId()));
    }

    @Test
    public void testParents() {
        List<String> edorgs = helper.getParentEdOrgs(school3);
        assertEquals(sea1.getEntityId(), edorgs.get(3));
        assertEquals(lea1.getEntityId(), edorgs.get(2));
        assertEquals(lea2.getEntityId(), edorgs.get(1));
        assertEquals(lea3.getEntityId(), edorgs.get(0));
        assertEquals(4, edorgs.size());
    }

    @Test
    public void testParentsOfSea() {
        List<String> edorgs = helper.getParentEdOrgs(sea1);
        assertEquals(0, edorgs.size());
    }

}
