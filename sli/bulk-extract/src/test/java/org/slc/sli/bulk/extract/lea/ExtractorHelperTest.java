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

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

@SuppressWarnings("unchecked")
public class ExtractorHelperTest {
    private ExtractorHelper helper;
    private Entity mockEntity;
    private DateHelper mockHelper;
    private EdOrgExtractHelper edOrgExtractHelper;
    private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Before
    public void setUp() {
        edOrgExtractHelper = Mockito.mock(EdOrgExtractHelper.class);
        helper = new ExtractorHelper();
        helper.setEdOrgExtractHelper(edOrgExtractHelper);
        mockEntity = Mockito.mock(Entity.class);
        mockHelper = Mockito.mock(DateHelper.class);
        helper.setDateHelper(mockHelper);
    }

    @After
    public void tearDown() {

    }

    /**
     * looks like helper.fetchCurrentSchoolsFromStudent is badly named as
     * it fetches edorgs for the student schools
     */
    @Test
    public void testExpiredSchools() {
        Map<String, List> lineages = ImmutableMap.of(
                "school1", (List)Arrays.asList(),
                "school2", (List)Arrays.asList(),
                "school3", (List)Arrays.asList("Proudhon", "Bakunin", "Kropotkin")
        );
        Mockito.when(edOrgExtractHelper.getEdOrgLineages()).thenReturn(lineages);

        helper.setDateHelper(null);
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        Map<String, Object> school = new HashMap<String, Object>();
        school.put("exitWithdrawDate", "1848-05-21");
        school.put("_id", "school1");

        Map<String, Object> school2 = new HashMap<String, Object>();
        school2.put("exitWithdrawDate", "1945-05-09");
        school2.put("_id", "school2");

        denormalized.put("schools", Arrays.asList(school, school2));
        
        Entity student = Mockito.mock(Entity.class);
        Mockito.when(student.getDenormalizedData()).thenReturn(denormalized);
        Assert.assertTrue("No schools should be returned", helper.fetchCurrentSchoolsForStudent(student).size() == 0);
        
        Map<String, Object> school3 = new HashMap<String, Object>();
        school3.put("_id", "school3");
        school3.put("exitWithdrawDate", "2048-05-09");

        denormalized.put("schools", Arrays.asList(school, school2, school3));
        Assert.assertTrue("There should be one school", helper.fetchCurrentSchoolsForStudent(student).size() == 3);
    }

    @Test
    public void testFetchCurrentSchoolsFromStudentNullChecks() {

        Map<String, List<Map<String, Object>>> denormalized = Mockito.mock(Map.class);
        Map<String, Object> school = Mockito.mock(Map.class);
        Mockito.when(school.get("_id")).thenReturn("school1");
        List<Map<String, Object>> schools = Arrays.asList(school);


        List<String> edorgs = Arrays.asList("One", "Two", "Three");
        Map<String, List> lineages = ImmutableMap.of(  "school1", (List)edorgs);
        Mockito.when(edOrgExtractHelper.getEdOrgLineages()).thenReturn(lineages);

        // No denormalized data
        Assert.assertTrue(helper.fetchCurrentSchoolsForStudent(mockEntity).size() == 0);

        // No school in schools
        Mockito.when(mockEntity.getDenormalizedData()).thenReturn(denormalized);
        Assert.assertTrue(helper.fetchCurrentSchoolsForStudent(mockEntity).size() == 0);

        // One school in schools
        Mockito.when(denormalized.get("schools")).thenReturn(schools);
        Mockito.when(denormalized.containsKey("schools")).thenReturn(true);
        Assert.assertTrue(helper.fetchCurrentSchoolsForStudent(mockEntity).size() == edorgs.size());

        // Date checking
        Mockito.when(mockHelper.isFieldExpired(school, "exitWithdrawDate")).thenReturn(true);
        Assert.assertTrue(helper.fetchCurrentSchoolsForStudent(mockEntity).size() == 0);

        Mockito.when(mockHelper.isFieldExpired(school, "exitWithdrawDate")).thenReturn(false);
        Assert.assertTrue(helper.fetchCurrentSchoolsForStudent(mockEntity).size() == 3);
    }

    @Test
    public void testFetchCurrentParentsFromStudentNullChecks() {

        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        // No denormalized data
        Assert.assertTrue(helper.fetchCurrentParentsFromStudent(mockEntity).size() == 0);

        // No items in data
        Mockito.when(mockEntity.getEmbeddedData()).thenReturn(embeddedData);
        Assert.assertTrue(helper.fetchCurrentParentsFromStudent(mockEntity).size() == 0);

        // contains parent associations
        Entity parentAssoc1 = Mockito.mock(Entity.class);
        Entity parentAssoc2 = Mockito.mock(Entity.class);
        Map<String, Object> body = Mockito.mock(Map.class);
        Map<String, Object> body2 = Mockito.mock(Map.class);
        Mockito.when(parentAssoc1.getBody()).thenReturn(body);
        Mockito.when(parentAssoc2.getBody()).thenReturn(body2);
        List<Entity> parentAssociations = Arrays.asList(parentAssoc1, parentAssoc2);
        embeddedData.put(EntityNames.STUDENT_PARENT_ASSOCIATION, parentAssociations);
        Assert.assertTrue(helper.fetchCurrentParentsFromStudent(mockEntity).size() == 0);

        Mockito.when(body.get(Mockito.eq(ParameterConstants.PARENT_ID))).thenReturn("ParentId123");
        Mockito.when(body2.get(Mockito.eq(ParameterConstants.PARENT_ID))).thenReturn("ParentId456");
        Assert.assertTrue(helper.fetchCurrentParentsFromStudent(mockEntity).size() == 2);
    }

    @Test
    public void testIsStaffAssignmentCurrent() {
        Map<String, Object> entityBody = new HashMap<String, Object>();
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);

        Mockito.when(mockHelper.isFieldExpired(entityBody, ParameterConstants.END_DATE)).thenReturn(true);
        Assert.assertFalse(helper.isStaffAssociationCurrent(mockEntity));

        Mockito.when(mockHelper.isFieldExpired(entityBody, ParameterConstants.END_DATE)).thenReturn(false);
        Assert.assertTrue(helper.isStaffAssociationCurrent(mockEntity));
    }
    
    @Test
    public void testBuildSubToParentEdOrgCache() {
    	EntityToEdOrgCache cache = new EntityToEdOrgCache();
    	cache.addEntry("lea-1", "school-1");
    	cache.addEntry("lea-1", "school-2");
    	cache.addEntry("lea-1", "school-3");
    	cache.addEntry("lea-2", "school-4");
    	cache.addEntry("lea-2", "school-5");
    	cache.addEntry("lea-3", "school-6");
    	
    	Map<String, Collection<String>> result = helper.buildSubToParentEdOrgCache(cache);
    	Assert.assertEquals(6, result.keySet().size());
    	Assert.assertEquals(Sets.newHashSet("lea-1"), result.get("school-1"));
    	Assert.assertEquals(Sets.newHashSet("lea-1"), result.get("school-2"));
    	Assert.assertEquals(Sets.newHashSet("lea-1"), result.get("school-3"));
    	Assert.assertEquals(Sets.newHashSet("lea-2"), result.get("school-4"));
    	Assert.assertEquals(Sets.newHashSet("lea-2"), result.get("school-5"));
    	Assert.assertEquals(Sets.newHashSet("lea-3"), result.get("school-6"));
    }

    @Test
    public void testFetchAllEdOrgs() {
        Map<String, List> lineages = ImmutableMap.of(
                "school1", (List)Arrays.asList("school1"),
                "school2", (List)Arrays.asList("school2", "Proudhon", "Kropotkin"),
                "school3", (List)Arrays.asList("school3", "Proudhon", "Bakunin", "Kropotkin")
        );
        Mockito.when(edOrgExtractHelper.getEdOrgLineages()).thenReturn(lineages);

        helper.setDateHelper(null);
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        Map<String, Object> school = new HashMap<String, Object>();
        school.put("exitWithdrawDate", "1848-05-21");
        school.put("_id", "school1");

        Map<String, Object> school2 = new HashMap<String, Object>();
        school2.put("exitWithdrawDate", "1945-05-09");
        school2.put("_id", "school2");

        denormalized.put("schools", Arrays.asList(school, school2));

        Entity student = Mockito.mock(Entity.class);
        Mockito.when(student.getDenormalizedData()).thenReturn(denormalized);

        Map<String, DateTime> result = helper.fetchAllEdOrgsForStudent(student);
        Assert.assertEquals(4, result.size());


        Map<String, Object> school3 = new HashMap<String, Object>();
        school3.put("_id", "school3");
        school3.put("exitWithdrawDate", "2048-05-09");
        denormalized.put("schools", Arrays.asList(school, school2, school3));

        result = helper.fetchAllEdOrgsForStudent(student);
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Kropotkin"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Proudhon"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Bakunin"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("school3"));
        Assert.assertEquals(DateTime.parse("1848-05-21", FMT), result.get("school1"));
        Assert.assertEquals(DateTime.parse("1945-05-09", FMT), result.get("school2"));
    }

    @Test
    public void testFetchAllEdOrgsNullDates() {
        Map<String, List> lineages = ImmutableMap.of(
                "school1", (List)Arrays.asList("school1"),
                "school2", (List)Arrays.asList("school2", "Proudhon", "Kropotkin"),
                "school3", (List)Arrays.asList("school3", "Proudhon", "Bakunin", "Kropotkin"),
                "school4", (List)Arrays.asList("school4", "Bakunin")
        );
        Mockito.when(edOrgExtractHelper.getEdOrgLineages()).thenReturn(lineages);

        helper.setDateHelper(null);
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        Map<String, Object> school = new HashMap<String, Object>();
        school.put("exitWithdrawDate", "1848-05-21");
        school.put("_id", "school1");

        Map<String, Object> school2 = new HashMap<String, Object>();
        school2.put("_id", "school2");

        denormalized.put("schools", Arrays.asList(school, school2));

        Entity student = Mockito.mock(Entity.class);
        Mockito.when(student.getDenormalizedData()).thenReturn(denormalized);

        Map<String, DateTime> result = helper.fetchAllEdOrgsForStudent(student);
        Assert.assertEquals(4, result.size());


        Map<String, Object> school3 = new HashMap<String, Object>();
        school3.put("_id", "school3");
        school3.put("exitWithdrawDate", "2048-05-09");
        denormalized.put("schools", Arrays.asList(school, school2, school3));

        Map<String, Object> school4 = new HashMap<String, Object>();
        school4.put("_id", "school4");
        denormalized.put("schools", Arrays.asList(school, school2, school3, school4));

        result = helper.fetchAllEdOrgsForStudent(student);
        Assert.assertEquals(7, result.size());
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Kropotkin"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Proudhon"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("Bakunin"));
        Assert.assertEquals(DateTime.parse("2048-05-09", FMT), result.get("school3"));
        Assert.assertEquals(DateTime.parse("1848-05-21", FMT), result.get("school1"));
        Assert.assertEquals(null, result.get("school2"));
        Assert.assertEquals(null, result.get("school4"));

    }
}
