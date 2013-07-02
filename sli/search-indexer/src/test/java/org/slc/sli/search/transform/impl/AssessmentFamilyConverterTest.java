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
package org.slc.sli.search.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slc.sli.common.constants.EntityNames.ASSESSMENT;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;

public class AssessmentFamilyConverterTest {
    @InjectMocks
    private AssessmentFamilyConverter converter = new AssessmentFamilyConverter();
    @Mock
    private SourceDatastoreConnector sourceDatastoreConnector;
    @Mock
    private IndexConfigStore indexConfigStore;
    private DBObject childFamily = buildFamily("Child", "family1", "parent");
    private DBObject parentFamily = buildFamily("Parent", "parent", "grandParent");
    private DBObject grandParentFamily = buildFamily("GrandParent", "grandParent", "ggparent");
    
    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        sourceDatastoreConnector = mock(SourceDatastoreConnector.class);
        indexConfigStore = Mockito.mock(IndexConfigStore.class);
        when(indexConfigStore.getConfig(ASSESSMENT)).thenReturn(new IndexConfig());
        converter.setIndexConfigStore(indexConfigStore);
        converter.setSourceDatastoreConnector(sourceDatastoreConnector);
        DBCursor assessmentCursor = TestUtils.buildMockCursor(buildAssessment());
        DBCursor emptyCursor = TestUtils.buildEmptyMockCursor();
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessment"), anyList(), any(DBObject.class))).thenReturn(emptyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessment"), anyList(), argThat(buildAssessmentFamilyReferenceMatcher("family1")))).thenReturn(assessmentCursor);
        DBCursor childFamilyCursor = TestUtils.buildMockCursor(childFamily);
        DBCursor parentFamilyCursor = TestUtils.buildMockCursor(parentFamily);
        DBCursor grandParentFamilyCursor = TestUtils.buildMockCursor(grandParentFamily);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), any(DBObject.class))).thenReturn(emptyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), argThat(TestUtils.buildIdMatcher("family1")))).thenReturn(childFamilyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), argThat(TestUtils.buildIdMatcher("parent")))).thenReturn(parentFamilyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), argThat(TestUtils.buildIdMatcher("grandParent")))).thenReturn(grandParentFamilyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), argThat(buildAssessmentFamilyReferenceMatcher("parent")))).thenReturn(childFamilyCursor);
        when(sourceDatastoreConnector.getDBCursor(any(String.class), eq("assessmentFamily"), anyList(), argThat(buildAssessmentFamilyReferenceMatcher("grandParent")))).thenReturn(parentFamilyCursor);
   }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleUpdateAF() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, Object> family = buildFamily("simple", "family1", "").toMap();
        List<Map<String, Object>> results = converter.treatment("Midgar", Action.UPDATE, family);
        assertEquals("simple", PropertyUtils.getProperty(results, "[0].body.assessmentFamilyHierarchyName"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateChildFamily() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        testHierarchicalUpdate(childFamily.toMap());
    }

    protected void testHierarchicalUpdate(Map<String, Object> family) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        List<Map<String, Object>> results = converter.treatment("Midgar", Action.UPDATE, family);
        assertEquals("GrandParent.Parent.Child", PropertyUtils.getProperty(results, "[0].body.assessmentFamilyHierarchyName"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateParentFamily() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        testHierarchicalUpdate(parentFamily.toMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateGrandParentFamily() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        testHierarchicalUpdate(grandParentFamily.toMap());
    }

    protected DBObject buildFamily(String title, String id, String parent) {
        Map<String, Object> family = new HashMap<String, Object>();
        Map<String, Object> familyBody = new HashMap<String, Object>();
        familyBody.put("assessmentFamilyTitle", title);
        familyBody.put("assessmentFamilyReference", parent);
        family.put("body", familyBody);
        family.put("_id", id);
        return new BasicDBObject(family);
    }

    private  DBObject buildAssessment() {
        DBObject assessment = new BasicDBObject();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("assessmentFamilyReference", "family1");
        assessment.put("body", body);
        return assessment;
    }
    
    private BaseMatcher<DBObject> buildAssessmentFamilyReferenceMatcher(final String id) {
        return new BaseMatcher<DBObject>() {
            
            @Override
            public boolean matches(Object arg0) {
                try {
                    return id.equals(((DBObject) arg0).get("body.assessmentFamilyReference"));
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            public void describeTo(Description arg0) {
            }
        };
    }
    

}
