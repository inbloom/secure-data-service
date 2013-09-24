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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


public class AttendanceExtractorTest {
    private AttendanceExtractor extractor;
    
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private ExtractorHelper mockHelper;
    @Mock
    private EntityToEdOrgCache mockCache;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private ExtractFile mockFile;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;

    @Mock
    private Entity mockEntity;
    
    Map<String, Object> entityBody;
    
    @Before
    public void setUp() {
        entityBody = new HashMap<String, Object>();
        MockitoAnnotations.initMocks(this);
        extractor = new AttendanceExtractor(mockExtractor, mockMap, mockRepo, mockHelper, mockCache, mockEdOrgExtractHelper);
        
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockCache.getEntriesById("student")).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testWriteOneAttendance() {
        Mockito.when(mockRepo.findEach(Mockito.eq("attendance"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity).iterator());
        entityBody.put("studentId", "student");
        extractor.extractEntities(null);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("attendance"));
    }
    
    @Test
    public void testWriteManyAttendances() {
        Mockito.when(mockRepo.findEach(Mockito.eq("attendance"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put("studentId", "student");
        extractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("attendance"));
    }
    
    @Test
    public void testWriteNoAttendances() {
        Mockito.when(mockRepo.findEach(Mockito.eq("attendance"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put("studentId", "student");
        Mockito.when(mockCache.getEntriesById("student")).thenReturn(new HashSet<String>());
        extractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("attendance"));
    }
}
