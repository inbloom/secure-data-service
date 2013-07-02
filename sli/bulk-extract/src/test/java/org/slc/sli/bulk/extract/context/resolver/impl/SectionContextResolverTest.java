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
package org.slc.sli.bulk.extract.context.resolver.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.domain.Entity;

/**
 * JUnit for SectionContextResolver
 * 
 * @author nbrown
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SectionContextResolverTest {
    
    @InjectMocks
    private StudentAssociationWalker walker = new StudentAssociationWalker();
    
    @Mock
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Mock
    private StudentContextResolver studentResolver;
    
    @InjectMocks
    private SectionContextResolver underTest = new SectionContextResolver(walker);
    
    @Test
    public void testSectionResolver() {
        when(edOrgResolver.findGoverningEdOrgs("osirisHigh")).thenReturn(
                new HashSet<String>(Arrays.asList("OsirisSchoolDistrict")));
        when(studentResolver.findGoverningEdOrgs("river")).thenReturn(
                new HashSet<String>(Arrays.asList("OsirisSchoolDistrict", "Academy")));
        when(studentResolver.findGoverningEdOrgs("simon")).thenReturn(
                new HashSet<String>(Arrays.asList("OsirisSchoolDistrict", "MedSchool")));
        Entity section = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolId", "osirisHigh");
        when(section.getBody()).thenReturn(body);
        when(section.getEntityId()).thenReturn("sectionId");
        Entity river = mock(Entity.class);
        Map<String, Object> riverBody = new HashMap<String, Object>();
        riverBody.put("studentId", "river");
        when(river.getBody()).thenReturn(riverBody);
        Entity simon = mock(Entity.class);
        Map<String, Object> simonBody = new HashMap<String, Object>();
        simonBody.put("studentId", "simon");
        when(simon.getBody()).thenReturn(simonBody);
        Map<String, List<Entity>> subdocs = new HashMap<String, List<Entity>>();
        subdocs.put("studentSectionAssociation", Arrays.asList(river, simon));
        when(section.getEmbeddedData()).thenReturn(subdocs);
        assertEquals(new HashSet<String>(Arrays.asList("OsirisSchoolDistrict", "Academy", "MedSchool")),
                underTest.findGoverningEdOrgs(section));
    }
    
}
