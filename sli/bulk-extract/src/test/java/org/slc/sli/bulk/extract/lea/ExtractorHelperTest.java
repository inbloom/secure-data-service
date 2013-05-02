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

package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;


public class ExtractorHelperTest {
    private ExtractorHelper helper;
    private Entity mockEntity;
    private DateHelper mockHelper;
    
    @Before
    public void setUp() {
        helper = new ExtractorHelper();
        mockEntity = Mockito.mock(Entity.class);
        mockHelper = Mockito.mock(DateHelper.class);
        helper.setDateHelper(mockHelper);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testFetchCurrentSchoolsFromStudentNullChecks() {
        
        Map<String, List<Map<String, Object>>> denormalized = Mockito.mock(Map.class);
        Map<String, Object> school = Mockito.mock(Map.class);
        List<Map<String, Object>> schools = Arrays.asList(school);
        List<String> edorgs = Arrays.asList("One", "Two", "Three");
        
        // No denormalized data
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == 0);
        
        // No items in data
        Mockito.when(mockEntity.getDenormalizedData()).thenReturn(denormalized);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == 0);
        
        // No edorgs in the schools
        Mockito.when(denormalized.get("schools")).thenReturn(schools);
        Mockito.when(denormalized.containsKey("schools")).thenReturn(true);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == 0);
        
        Mockito.when(school.get("edOrgs")).thenReturn(edorgs);
        Mockito.when(school.containsKey("edOrgs")).thenReturn(false);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == 0);
        
        Mockito.when(school.get("edOrgs")).thenReturn(edorgs);
        Mockito.when(school.containsKey("edOrgs")).thenReturn(true);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == edorgs.size());
        
        // Date checking
        Mockito.when(mockHelper.isFieldExpired(school, "exitWithdrawDate")).thenReturn(true);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == 0);
        
        Mockito.when(mockHelper.isFieldExpired(school, "exitWithdrawDate")).thenReturn(false);
        Assert.assertTrue(helper.fetchCurrentSchoolsFromStudent(mockEntity).size() == edorgs.size());
    }
}
