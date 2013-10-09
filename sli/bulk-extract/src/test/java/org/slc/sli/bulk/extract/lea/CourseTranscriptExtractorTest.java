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
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class CourseTranscriptExtractorTest {

    private CourseTranscriptExtractor extractor;

    @Mock
    private Repository<Entity> mockRepo;

    @Mock
    private ExtractFileMap mockMap;

    @Mock
    private EntityExtractor mockExtractor;

    @Mock
    private ExtractFile mockFile;

    @Mock
    private EntityToEdOrgCache mockEdorgCache;

    @Mock
    private EntityToEdOrgDateCache mockStudentDateCache;

    @Mock
    private EntityToEdOrgDateCache mockStudentAcademicRecordCache;

    @Mock
    private Entity mockEntity;

    private Map<String, Object> entityBody;

    @Mock
    private Entity mockTransitiveEntity;

    private Map<String, Object> transitiveEntityBody;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        entityBody = new HashMap<String, Object>();
        entityBody.put(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, "sar1");
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.COURSE_TRANSCRIPT);
        Mockito.when(mockEntity.getEntityId()).thenReturn("ct1");
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);

        transitiveEntityBody = new HashMap<String, Object>();
        transitiveEntityBody.put(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, "sar2");
        Mockito.when(mockTransitiveEntity.getType()).thenReturn(EntityNames.COURSE_TRANSCRIPT);
        Mockito.when(mockTransitiveEntity.getEntityId()).thenReturn("ct2");
        Mockito.when(mockTransitiveEntity.getBody()).thenReturn(transitiveEntityBody);

        mockEdorgCache = new EntityToEdOrgCache();
        mockEdorgCache.addEntry("LEA", "edorgId1");
        mockEdorgCache.addEntry("LEA", "edorgId2");

        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
    }

    @Test
    public void testEdorgReferenced() {
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId1"));
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testStudentReferenced() {
        Map<String, DateTime> leas = new HashMap<String, DateTime>();
        leas.put("LEA", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
        Mockito.when(mockStudentDateCache.getEntriesById("student1")).thenReturn(leas);

        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
            .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.STUDENT_ID, "student1");
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testStudentAcademicReferencedReferenced() {
        Map<String, DateTime> leas = new HashMap<String, DateTime>();
        leas.put("LEA", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
        Mockito.when(mockStudentAcademicRecordCache.getEntriesById("sar1")).thenReturn(leas);

        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
            .thenReturn(Arrays.asList(mockEntity).iterator());
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId1"));
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testThreeReferencesOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId1"));

        entityBody.put(ParameterConstants.STUDENT_ID, "student1");
        Map<String, DateTime> leas = new HashMap<String, DateTime>();
        leas.put("LEA", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
        Mockito.when(mockStudentAcademicRecordCache.getEntriesById("sar1")).thenReturn(leas);
        Mockito.when(mockStudentDateCache.getEntriesById("student1")).thenReturn(leas);
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId1"));
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId3"));
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA-1")).thenReturn(mockFile);
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

    @Test
    public void testExtractNoEntityBecauseShouldNotExtract() {
        Mockito.when(mockRepo.findEach(Mockito.eq("courseTranscript"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Arrays.asList("edorgId1"));
        CourseTranscriptExtractor realExtractor = new CourseTranscriptExtractor(mockExtractor, mockMap, mockRepo,
                mockEdorgCache, mockStudentDateCache);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(false).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(mockStudentAcademicRecordCache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("courseTranscript"));
    }

}
