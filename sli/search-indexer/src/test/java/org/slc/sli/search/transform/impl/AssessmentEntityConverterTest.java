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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
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
import org.mockito.MockitoAnnotations;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;

/**
 * JUnit for assessment entity converter
 */
public class AssessmentEntityConverterTest {
    
    @InjectMocks
    AssessmentEntityConverter assessmentConverter;
    
    @Mock
    SourceDatastoreConnector sourceDatastoreConnector;
    
    @Mock
    IndexConfigStore indexConfigStore;
    
    @Mock
    DBCursor apdCursor;
    
    @Mock
    DBCursor assessmentCursor;
    
    private final static String INDEX = "Midgar";
    
    private IndexConfig assessmentPeriodDescriptorConfig = new IndexConfig();
    private Map<String, Object> assessment;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        assessment = buildAssessmentMap();
        
        sourceDatastoreConnector = Mockito.mock(SourceDatastoreConnector.class);
        indexConfigStore = Mockito.mock(IndexConfigStore.class);
        apdCursor = Mockito.mock(DBCursor.class);
        assessmentCursor = Mockito.mock(DBCursor.class);
        MockitoAnnotations.initMocks(this);
        
        when(
                sourceDatastoreConnector
                        .getDBCursor(anyString(), eq(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR),
                                anyList(), any(BasicDBObject.class))).thenReturn(apdCursor);
        when(
                sourceDatastoreConnector.getDBCursor(anyString(), eq(AssessmentEntityConverter.ASSESSMENT), anyList(),
                        any(BasicDBObject.class))).thenReturn(assessmentCursor);
        when(indexConfigStore.getConfig(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR)).thenReturn(
                assessmentPeriodDescriptorConfig);
        when(indexConfigStore.getConfig(AssessmentEntityConverter.ASSESSMENT)).thenReturn(new IndexConfig());
    }
    
    @Test
    public void deleteActionShouldNotChangeInput() {
        Map<String, Object> clone = buildAssessmentMap();
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.DELETE, assessment);
        assertEquals(clone, entities.get(0));
    }
    
    @Test
    public void assessmentPeriodIdShouldNotBeIndexed() {
        when(apdCursor.hasNext()).thenReturn(false);
        
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        assertNotNull(entities.get(0).get("body"));
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNull(body.get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR_ID));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assessmentPeriodDescriptorShouldBeDenormalizedIntoAssessment() {
        when(apdCursor.hasNext()).thenReturn(true);
        when(apdCursor.next()).thenReturn(buildAssessmentPeriodDescriptor());
        
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        assertNotNull(entities.get(0).get("body"));
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNotNull(body.get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR));
        Map<String, Object> assessmentPeriodDescriptor = (Map<String, Object>) body
                .get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR);
        assertEquals(assessmentPeriodDescriptor.get("codeValue"), "red");
    }
    
    @Test
    public void ifBodyIsEmptyWeShouldPullDataFromMongo() {
        when(assessmentCursor.hasNext()).thenReturn(true);
        when(assessmentCursor.next()).thenReturn(new BasicDBObject(buildAssessmentMap()));
        
        assessment.remove("body");
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        // should have 1 entity
        assertEquals(1, entities.size());
        // body should not be null
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNotNull(body);
        assertEquals("SAT", body.get("assessmentTitle"));
    }
    
    private DBObject buildAssessmentPeriodDescriptor() {
        DBObject assessmentPeriodDescriptor = new BasicDBObject();
        Map<String, Object> apdBody = new HashMap<String, Object>();
        apdBody.put("codeValue", "red");
        assessmentPeriodDescriptor.put("body", apdBody);
        return assessmentPeriodDescriptor;
    }
    
    private Map<String, Object> buildAssessmentMap() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR_ID, "apd_id");
        body.put("assessmentFamilyReference", "family1");
        body.put("assessmentTitle", "SAT");
        Map<String, Object> assessment = new HashMap<String, Object>();
        assessment.put("type", "assessment");
        assessment.put("_id", "assessment_id");
        assessment.put("body", body);
        return assessment;
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBuildingFamilyOnAssessmentUpdate() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        DBCursor family1Cursor = buildMockCursor(buildFamily("family1", "ChildFamily", "family2"));
        when(
                sourceDatastoreConnector.getDBCursor(anyString(), eq(AssessmentEntityConverter.ASSESSMENT_FAMILY_COLLECTION), anyList(),
                        argThat(buildIdMatcher("family1")))).thenReturn(family1Cursor);
        DBCursor family2Cursor = buildMockCursor(buildFamily("family2", "ParentFamily", null));
        when(
                sourceDatastoreConnector.getDBCursor(anyString(), eq(AssessmentEntityConverter.ASSESSMENT_FAMILY_COLLECTION), anyList(),
                        argThat(buildIdMatcher("family2")))).thenReturn(family2Cursor);
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        assertEquals(null, PropertyUtils.getProperty(entities, "[0].body.assessmentFamilyReference"));
        assertEquals("ParentFamily.ChildFamily",
                PropertyUtils.getProperty(entities, "[0].body.assessmentFamilyHierarchyName"));
        
    }
    
    @Test
    public void actionShouldRemainSame() {
        Action origAction = Action.DELETE;
        Action newAction = assessmentConverter.convertAction(origAction);
        assertEquals(origAction, newAction);
        origAction = Action.INDEX;
        newAction = assessmentConverter.convertAction(origAction);
        assertEquals(origAction, newAction);
        origAction = Action.UPDATE;
        newAction = assessmentConverter.convertAction(origAction);
        assertEquals(origAction, newAction);
        origAction = Action.UPDATE;
        newAction = assessmentConverter.convertAction(origAction);
        assertEquals(origAction, newAction);
    }

    private DBObject buildFamily(String id, String title, String parent) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("assessmentFamilyTitle", title);
        body.put("assessmentFamilyReference", parent);
        return BasicDBObjectBuilder.start("body", body).add("_id", id).get();
    }
    
    private BaseMatcher<DBObject> buildIdMatcher(final String id) {
        return new BaseMatcher<DBObject>() {
            
            @Override
            public boolean matches(Object arg0) {
                return id.equals(((DBObject) arg0).get("_id"));
            }
            
            @Override
            public void describeTo(Description arg0) {
            }
        };
    }
    
    private DBCursor buildMockCursor(final DBObject obj) {
        DBCursor cursor = mock(DBCursor.class);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(obj);
        return cursor;
    }
    
}
