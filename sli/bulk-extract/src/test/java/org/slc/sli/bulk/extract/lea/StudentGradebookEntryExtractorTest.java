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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentGradebookEntryExtractorTest {
    private StudentGradebookEntryExtractor extractor;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFile mockFile;
    
    @Mock
    private EntityToEdOrgDateCache mockStudentCache;
    
    @Mock
    private Entity mockEntity;

    @Mock
    private Entity gradebookEntryEntity;
    
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    
    private Map<String, Object> entityBody;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extractor = new StudentGradebookEntryExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        entityBody = new HashMap<String, Object>();
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.STUDENT_GRADEBOOK_ENTRY);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);

        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA", DateTime.now());

        Mockito.when(mockStudentCache.getEntriesById("studentId")).thenReturn(edOrgDate);
    }
    
    @Test
    public void testWriteOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq("studentGradebookEntry"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "studentId");

        StudentGradebookEntryExtractor spyExtractor = Mockito.spy(extractor);
        Mockito.doReturn(true).when(spyExtractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        spyExtractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("studentGradebookEntry"));
    }
    
    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq("studentGradebookEntry"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "studentId");

        StudentGradebookEntryExtractor spyExtractor = Mockito.spy(extractor);
        Mockito.doReturn(true).when(spyExtractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        spyExtractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("studentGradebookEntry"));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq("studentGradebookEntry"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        entityBody.put(ParameterConstants.STUDENT_ID, "studentId");

        StudentGradebookEntryExtractor spyExtractor = Mockito.spy(extractor);
        Mockito.doReturn(false).when(spyExtractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        spyExtractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("studentGradebookEntry"));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put(ParameterConstants.STUDENT_ID, "studentId99");
        Mockito.when(mockRepo.findEach(Mockito.eq("studentGradebookEntry"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("studentGradebookEntry"));
    }
}
