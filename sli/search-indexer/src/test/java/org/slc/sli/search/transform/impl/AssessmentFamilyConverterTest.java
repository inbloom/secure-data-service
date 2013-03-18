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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class AssessmentFamilyConverterTest {
    @InjectMocks
    private AssessmentFamilyConverter converter = new AssessmentFamilyConverter();
    @Mock
    private SourceDatastoreConnector sourceDatastoreConnector;
    @Mock
    private IndexConfigStore indexConfigStore;
    
    @Before
    public void setup() {
        sourceDatastoreConnector = mock(SourceDatastoreConnector.class);
        indexConfigStore = Mockito.mock(IndexConfigStore.class);
        when(indexConfigStore.getConfig(AssessmentEntityConverter.ASSESSMENT)).thenReturn(new IndexConfig());
        converter.setIndexConfigStore(indexConfigStore);
        converter.setSourceDatastoreConnector(sourceDatastoreConnector);
   }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleUpdateAF() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DBObject assessment = buildAssessment();
        DBCursor cursor = TestUtils.buildMockCursor(assessment);
        when(sourceDatastoreConnector.getDBCursor(eq("assessment"), anyList(), any(DBObject.class))).thenReturn(cursor);
        Map<String, Object> family = buildFamily("simple", "family1", "").toMap();
        List<Map<String, Object>> results = converter.treatment("Midgar", Action.UPDATE, family);
        assertEquals("simple", PropertyUtils.getProperty(results, "[0].body.assessmentFamilyHierarchyName"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateSubFamily() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DBCursor assessmentCursor = TestUtils.buildMockCursor(buildAssessment());
        DBCursor parentFamilyCursor = TestUtils.buildMockCursor(buildFamily("Parent", "parent", "grandParent"));
        DBCursor grandParentFamilyCursor = TestUtils.buildMockCursor(buildFamily("GrandParent", "grandParent", "ggparent"));
        DBCursor emptyCursor = TestUtils.buildEmptyMockCursor();
        when(sourceDatastoreConnector.getDBCursor(eq("assessment"), anyList(), any(DBObject.class))).thenReturn(assessmentCursor);
        when(sourceDatastoreConnector.getDBCursor(eq("assessmentFamily"), anyList(), any(DBObject.class))).thenReturn(emptyCursor);
        when(sourceDatastoreConnector.getDBCursor(eq("assessmentFamily"), anyList(), argThat(TestUtils.buildIdMatcher("parent")))).thenReturn(parentFamilyCursor);
        when(sourceDatastoreConnector.getDBCursor(eq("assessmentFamily"), anyList(), argThat(TestUtils.buildIdMatcher("grandParent")))).thenReturn(grandParentFamilyCursor);
        Map<String, Object> family = buildFamily("Child", "family1", "parent").toMap();
        List<Map<String, Object>> results = converter.treatment("Midgar", Action.UPDATE, family);
        assertEquals("GrandParent.Parent.Child", PropertyUtils.getProperty(results, "[0].body.assessmentFamilyHierarchyName"));
        
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
    
}
