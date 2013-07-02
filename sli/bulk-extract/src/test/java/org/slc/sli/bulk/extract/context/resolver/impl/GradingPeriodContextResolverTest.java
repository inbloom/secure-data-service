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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.domain.Entity;

/**
 * JUnit for GradingPeriodContextResolver
 * 
 * @author nbrown
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class GradingPeriodContextResolverTest {
    @InjectMocks
    private GradingPeriodContextResolver underTest = new GradingPeriodContextResolver();
    
    @Mock
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Test
    public void testGradingPeriodResolver() {
        when(edOrgResolver.findGoverningEdOrgs("osirisHigh")).thenReturn(
                new HashSet<String>(Arrays.asList("OsirisSchoolDistrict")));
        Entity gradingPeriod = mock(Entity.class);
        Map<String, Object> gradingPeriodIdentity = new HashMap<String, Object>();
        gradingPeriodIdentity.put("schoolId", "osirisHigh");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("gradingPeriodIdentity", gradingPeriodIdentity);
        when(gradingPeriod.getBody()).thenReturn(body);
        assertEquals(new HashSet<String>(Arrays.asList("OsirisSchoolDistrict")), underTest.findGoverningEdOrgs(gradingPeriod));

    }

}
