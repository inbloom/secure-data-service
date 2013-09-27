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
package org.slc.sli.domain.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.ValidationTestUtils;

public class EdOrgHierarchyHelperTest {

    // entity types
    public static final String SCHOOL = "School";
    public static final String LEA = "Local Education Agency";
    public static final String SEA = "State Education Agency";

    // attribute names
    public static final String PARENT_ED_ORG_REF = "parentEducationAgencyReference";
    
    EdOrgHierarchyHelper underTest;

    @Mock
    Repository<Entity> repo;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        repo = Mockito.mock(Repository.class);
        underTest = new EdOrgHierarchyHelper(repo);
    }
    
    @Test
    public void validateTypes() {
        Entity a = buildEntityType("School");
        assertTrue(underTest.isSchool(a));
        Entity b = buildEntityType("Local Education Agency");
        assertTrue(underTest.isLEA(b));
        Entity c = buildEntityType("State Education Agency");
        assertTrue(underTest.isSEA(c));
    }
    
    public void getTopLEAChainIsNotBroken() {
        Entity school = buildEntityType("School");
        Entity lea = buildEntityType("Local Education Agency");
        school.getBody().put("parentEducationAgencyReference", lea.getEntityId());
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, lea.getEntityId())).thenReturn(lea);
        assertEquals(lea, underTest.getTopLEAOfEdOrg(school));
    }

    public void getNullIfLEAChainIsBroken() {
        Entity school = buildEntityType("School");
        school.getBody().put("parentEducationAgencyReference", "non-exist");
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "non-exist")).thenReturn(null);
        assertNull(underTest.getTopLEAOfEdOrg(school));
    }
    
    public void getSEAIfChainIsNotBroken() {
        Entity school = buildEntityType("School");
        Entity lea = buildEntityType("Local Education Agency");
        Entity sea = buildEntityType("State Education Agency");
        school.getBody().put("parentEducationAgencyReference", lea.getEntityId());
        lea.getBody().put("parentEducationAgencyReference", sea.getEntityId());
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, lea.getEntityId())).thenReturn(lea);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, sea.getEntityId())).thenReturn(sea);
        assertEquals(sea, underTest.getSEAOfEdOrg(school));
    }

    public void getNullIfSEAChainIsBroken() {
        Entity school = buildEntityType("School");
        school.getBody().put("parentEducationAgencyReference", "non-exist");
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "non-exist")).thenReturn(null);
        assertNull(underTest.getSEAOfEdOrg(school));

        Entity lea = buildEntityType("Local Education Agency");
        lea.getBody().put("parentEducationAgencyReference", "non-exist");
        assertNull(underTest.getSEAOfEdOrg(lea));
    }

    private Entity buildEntityType(String string) {
        Entity e = ValidationTestUtils.makeDummyEntity("educationOrganization", string);
        e.getBody().put("organizationCategories", Arrays.asList(string));
        return e;
    }

    /**
     * Make sure that {@link EdOrgHierarchyHelper#getAncestorsOfEdOrg(org.slc.sli.domain.Entity)} handles
     * cycles of ed orgs.
     */
    @Test
    public void testGetAncestorsOfEdOrgWithCycle() {
        Entity sea = buildEntityType(SEA);
        Entity lea = buildEntityType(LEA);
        Entity school = buildEntityType(SCHOOL);

        school.getBody().put(PARENT_ED_ORG_REF, Arrays.asList(lea.getEntityId()));
        // here's the cycle (LEA is a parent of school and the school is a parent of the LEA)
        lea.getBody().put(PARENT_ED_ORG_REF, Arrays.asList(sea.getEntityId(), school.getEntityId()));
        sea.getBody().put(PARENT_ED_ORG_REF, Arrays.asList());

        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, sea.getEntityId())).thenReturn(sea);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, lea.getEntityId())).thenReturn(lea);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, school.getEntityId())).thenReturn(school);

        Set<Entity> ancestors = underTest.getAncestorsOfEdOrg(school);

        assertNotNull(ancestors);

        assertTrue("LEA must be an ancestor", ancestors.contains(lea));
        assertTrue("school should be contained in the results as well, per required behavior", ancestors.contains(school));
        assertEquals("unexpected ancestors", 2, ancestors.size());
    }

    
}
