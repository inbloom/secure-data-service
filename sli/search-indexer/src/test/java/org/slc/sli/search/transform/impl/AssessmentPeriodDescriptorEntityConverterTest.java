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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.slc.sli.search.transform.impl.AssessmentPeriodDescriptorEntityConverter.ASSESSMENT;
import static org.slc.sli.search.transform.impl.AssessmentPeriodDescriptorEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR;

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
public class AssessmentPeriodDescriptorEntityConverterTest {
    
    @InjectMocks
    AssessmentPeriodDescriptorEntityConverter converter;

    @Mock
    SourceDatastoreConnector sourceDatastoreConnector;
    
    @Mock
    IndexConfigStore indexConfigStore;
    
    @Mock
    DBCursor cursor;

    private final static String INDEX = "Midgar";

    private IndexConfig assessmentConfig = new IndexConfig();
    private Map<String, Object> assessmentPeriodDescriptor;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        assessmentPeriodDescriptor = buildAssessmentPeriodDescriptorMap();

        sourceDatastoreConnector = Mockito.mock(SourceDatastoreConnector.class);
        indexConfigStore = Mockito.mock(IndexConfigStore.class);
        cursor = Mockito.mock(DBCursor.class);
        MockitoAnnotations.initMocks(this);
        
        when(sourceDatastoreConnector.getDBCursor(anyString(), eq(AssessmentPeriodDescriptorEntityConverter.ASSESSMENT), anyList(), any(BasicDBObject.class))).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true);
        when(indexConfigStore.getConfig(ASSESSMENT)).thenReturn(assessmentConfig);
    }

    @Test
    public void deleteShouldReturnEmptyList() {
        List<Map<String, Object>> entities = converter.treatment(INDEX, Action.DELETE, assessmentPeriodDescriptor);
        assertEquals(0, entities.size());
    }

    @Test
    public void emptyListIfNoAssessmentReferencesThisAPD() {
        when(cursor.hasNext()).thenReturn(false);
        List<Map<String, Object>> entities = converter.treatment(INDEX, Action.INDEX, assessmentPeriodDescriptor);
        assertEquals(0, entities.size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertToAssessmentEntities() {
        when(cursor.hasNext()).thenReturn(true, true, false);
        when(cursor.next()).thenReturn(buildAssessmentMap());
        List<Map<String, Object>> entities = converter.treatment(INDEX, Action.INDEX, assessmentPeriodDescriptor);
        assertEquals(2, entities.size());
        Map<String, Object> body = (Map<String, Object>) entities.get(0).get("body");
        assertNotNull(body);
        Map<String, Object> apd = (Map<String, Object>) (body.get(ASSESSMENT_PERIOD_DESCRIPTOR));
        assertEquals("red", apd.get("codeValue"));
    }

    private Map<String, Object> buildAssessmentPeriodDescriptorMap() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("codeValue", "red");
        Map<String, Object> assessmentPeriodDescriptor = new HashMap<String, Object>();
        assessmentPeriodDescriptor.put("type", "assessmentPeriodDescriptor");
        assessmentPeriodDescriptor.put("_id", "apd_id");
        assessmentPeriodDescriptor.put("body", body);
        return assessmentPeriodDescriptor;
    }

    private DBObject buildAssessmentMap() {
        DBObject assessment = new BasicDBObject();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(AssessmentEntityConverter.ASSESSMENT_PERIOD_DESCRIPTOR_ID, "apd_id");
        assessment.put("type", "assessment");
        assessment.put("_id", "assessment_id");
        assessment.put("body", body);
        return assessment;
    }
    
}
