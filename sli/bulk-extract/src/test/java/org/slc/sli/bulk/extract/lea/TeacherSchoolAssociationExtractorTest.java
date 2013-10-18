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

import java.util.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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
public class TeacherSchoolAssociationExtractorTest {
    private TeacherSchoolAssociationExtractor extractor;
    
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private EntityToEdOrgCache mockCache;
    @Mock
    private Entity mockEntity;
    @Mock
    private Entity mockEntity2;
    @Mock
    private ExtractFile mockFile;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    
    private Map<String, Object> entityBody;
    private EntityToEdOrgDateCache staffToLeaDateCache;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        entityBody = new HashMap<String, Object>();
        extractor = new TeacherSchoolAssociationExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        entityBody.put(ParameterConstants.TEACHER_ID, "Staff1");
        entityBody.put(ParameterConstants.SCHOOL_ID, "LEA");
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);

        Map<String, Object> entityBody2 = new HashMap<String, Object>();
        entityBody2.put(ParameterConstants.TEACHER_ID, "Staff1");
        entityBody2.put(ParameterConstants.SCHOOL_ID, "LEA2");
        Mockito.when(mockEntity2.getBody()).thenReturn(entityBody2);

        Mockito.when(mockMap.getExtractFileForEdOrg(Matchers.anyString())).thenReturn(mockFile);
        Mockito.when(mockEntity.getEntityId()).thenReturn("Staff1");
        Mockito.when(mockEntity2.getEntityId()).thenReturn("Staff1");
    }
    
    @Test
    public void testExtractOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);


        staffToLeaDateCache = new EntityToEdOrgDateCache();
        DateTime dt = DateTime.now();
        staffToLeaDateCache.addEntry("Staff1", "LEA", dt);

        Entity mSeoa = makeSeoa("2007-01-01", null);

        Mockito.when(mockEdOrgExtractHelper.retrieveSEOAS(Matchers.eq("Staff1"), Matchers.eq("LEA"))).thenReturn(Arrays.asList(mSeoa));

        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
    }
    
    @Test
    public void testExtractManyEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity, mockEntity2).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);

        staffToLeaDateCache = new EntityToEdOrgDateCache();
        staffToLeaDateCache.addEntry("Staff1", "LEA", DateTime.parse("20009-01-01"));
        staffToLeaDateCache.addEntry("Staff1", "LEA2", DateTime.parse("2000-01-01"));

        Entity mSeoa1 = makeSeoa("2007-01-01", "2008-01-01");
        Entity mSeoa2 = makeSeoa("2008-01-01", null);

        Mockito.when(mockEdOrgExtractHelper.retrieveSEOAS(Matchers.eq("Staff1"), Matchers.eq("LEA"))).thenReturn(Arrays.asList(mSeoa1, mSeoa2));

        Entity mSeoa1L2 = makeSeoa("1999-01-01", "2008-01-01");
        Entity mSeoa2L2 = makeSeoa("2000-01-01", "2002-01-01");

        Mockito.when(mockEdOrgExtractHelper.retrieveSEOAS(Matchers.eq("Staff1"), Matchers.eq("LEA2"))).thenReturn(Arrays.asList(mSeoa1L2, mSeoa2L2));

                extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockEntity2), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        staffToLeaDateCache = new EntityToEdOrgDateCache();
        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put(ParameterConstants.TEACHER_ID, "STAFFX");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        staffToLeaDateCache = new EntityToEdOrgDateCache();
        staffToLeaDateCache.addEntry("Staff1", "LEA", DateTime.parse("2000-01-01"));
        staffToLeaDateCache = new EntityToEdOrgDateCache();
        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
    }

    private Entity makeSeoa(String startDate, String endDate) {
        Entity mSeoa = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, startDate);

        if(endDate != null) {
            body.put(ParameterConstants.END_DATE, endDate);
        }

        Mockito.mock(Entity.class);
        Mockito.when(mSeoa.getBody()).thenReturn(body);
        Mockito.when(mSeoa.getType()).thenReturn(EntityNames.STAFF_ED_ORG_ASSOCIATION);

        return mSeoa;
    }
}
