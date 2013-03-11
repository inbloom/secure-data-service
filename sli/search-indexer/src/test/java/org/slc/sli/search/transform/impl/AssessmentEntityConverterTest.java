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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
    DBCursor cursor;

    private final static String INDEX = "Midgar";

    private IndexConfig assessmentPeriodDescriptorConfig = new IndexConfig();
    private Map<String, Object> assessment;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        assessment = buildAssessmentMap();

        sourceDatastoreConnector = Mockito.mock(SourceDatastoreConnector.class);
        indexConfigStore = Mockito.mock(IndexConfigStore.class);
        cursor = Mockito.mock(DBCursor.class);
        MockitoAnnotations.initMocks(this);
        
        when(sourceDatastoreConnector.getDBCursor(anyString(), eq(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR), anyList(), any(BasicDBObject.class))).thenReturn(cursor);
        when(indexConfigStore.getConfig(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR)).thenReturn(assessmentPeriodDescriptorConfig);
    }

    @Test
    public void deleteActionShouldNotChangeInput() {
        Map<String, Object> clone = buildAssessmentMap();
        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.DELETE, assessment);
        assertEquals(clone, entities.get(0));
    }
    
    @Test
    public void assessmentPeriodIdShouldNotBeIndexed() {
        when(cursor.hasNext()).thenReturn(false);

        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        assertNotNull(entities.get(0).get("body"));
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNull(body.get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR_ID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assessmentPeriodDescriptorShouldBeDenormalizedIntoAssessment() {
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenReturn(buildAssessmentPeriodDescriptor());

        List<Map<String, Object>> entities = assessmentConverter.treatment(INDEX, Action.INDEX, assessment);
        assertNotNull(entities.get(0).get("body"));
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNotNull(body.get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR));
        Map<String, Object> assessmentPeriodDescriptor = (Map<String, Object>) body.get(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR);
        assertEquals(assessmentPeriodDescriptor.get("codeValue"), "red");
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
        Map<String, Object> assessment = new HashMap<String, Object>();
        assessment.put("type", "assessment");
        assessment.put("_id", "assessment_id");
        assessment.put("body", body);
        return assessment;
    }
    
    
}
