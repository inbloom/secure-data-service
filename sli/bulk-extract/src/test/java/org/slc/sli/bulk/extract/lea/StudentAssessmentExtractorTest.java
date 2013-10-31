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
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentAssessmentExtractorTest {
    
    private StudentAssessmentExtractor extractor;
    
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
    private EdOrgExtractHelper mockHelper;

    private Map<String, Object> entityBody;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extractor = new StudentAssessmentExtractor(mockExtractor, mockMap, mockRepo, mockHelper);
        entityBody = new HashMap<String, Object>();
        entityBody.put("administrationDate", "2009-12-21");
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.STUDENT_ASSESSMENT);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);

        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA", DateTime.now());
        Mockito.when(mockStudentCache.getEntriesById("student")).thenReturn(edOrgDate);
    }
    
    
    @Test
    public void testWriteOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_ASSESSMENT), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
    }
    
    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_ASSESSMENT), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
    }

    @Test
    public void testWriteSomeEntities() {
        Entity saEntity = Mockito.mock(Entity.class);
        Map<String, Object> saEntityBody = new HashMap<String, Object>();
        saEntityBody.put("administrationDate", "3000-12-21");
        saEntityBody.put(ParameterConstants.STUDENT_ID, "student");
        Mockito.when(saEntity.getBody()).thenReturn(saEntityBody);
        Mockito.when(saEntity.getType()).thenReturn(EntityNames.STUDENT_ASSESSMENT);

        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_ASSESSMENT), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                        Arrays.asList(mockEntity, saEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");

        extractor.extractEntities(mockStudentCache);

        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
        Mockito.verify(mockExtractor, Mockito.times(0)).extractEntity(Mockito.eq(saEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_ASSESSMENT), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put(ParameterConstants.STUDENT_ID, "STUDENT1");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_ASSESSMENT), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STUDENT_ASSESSMENT));
    }

}
