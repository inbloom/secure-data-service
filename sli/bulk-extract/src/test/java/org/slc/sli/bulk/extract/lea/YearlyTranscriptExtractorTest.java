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
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

public class YearlyTranscriptExtractorTest {

    private YearlyTranscriptExtractor extractor;
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
    private EntityToEdOrgDateCache mockStudentAcademicRecordCache;

    @Mock
    private Entity mockEntity;

    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;

    private Map<String, Object> entityBody;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extractor = new YearlyTranscriptExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        entityBody = new HashMap<String, Object>();
        entityBody.put(ParameterConstants.SCHOOL_YEAR, "2010-2011");
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getType()).thenReturn("yearlyTranscript");
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);

        Map<String, DateTime> edorgDatedCache = new HashMap<String, DateTime>();
        edorgDatedCache.put("LEA", DateTime.parse("2011-01-01", DateHelper.getDateTimeFormat()));
        Mockito.when(mockStudentCache.getEntriesById("student")).thenReturn(edorgDatedCache);
    }

    @Test
    public void testDoNotWriteDatedBasedEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");
        entityBody.put(ParameterConstants.SCHOOL_YEAR, "2012-2013");
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("yearlyTranscript"));
    }

    @Test
    public void testWriteOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("yearlyTranscript"));
    }

    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("yearlyTranscript"));
    }

    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("yearlyTranscript"));
    }

    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put(ParameterConstants.STUDENT_ID, "STUDENT1");
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(mockStudentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("yearlyTranscript"));
    }

    @Test
    public void testStudentAcademicRecordCache() {
        Mockito.when(mockRepo.findEach(Mockito.eq("yearlyTranscript"), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student");

        Entity sar1 = Mockito.mock(Entity.class);
        Map<String, Object> body = Mockito.mock(Map.class);
        Mockito.when(sar1.getBody()).thenReturn(body);
        Mockito.when(sar1.getEntityId()).thenReturn("sar1");
        Mockito.when(body.get(Mockito.eq(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID))).thenReturn("sar1");
        List<Entity> sars = Arrays.asList(sar1);
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();
        embeddedData.put(EntityNames.STUDENT_ACADEMIC_RECORD, sars);
        Mockito.when(mockEntity.getEmbeddedData()).thenReturn(embeddedData);

        extractor.extractEntities(mockStudentCache);

        Assert.assertTrue(extractor.getStudentAcademicRecordDateCache().getEntriesById("sar1").containsKey("LEA"));
    }

}
