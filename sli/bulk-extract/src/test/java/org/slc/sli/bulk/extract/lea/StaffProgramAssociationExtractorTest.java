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
import org.junit.After;
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

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StaffProgramAssociationExtractorTest {
    private StaffProgramAssociationExtractor extractor;
    
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private Entity mockEntity;
    @Mock
    private ExtractFile mockFile;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    
    private Map<String, Object> entityBody;
    private EntityToEdOrgDateCache staffToLeaCache;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        entityBody = new HashMap<String, Object>();
        entityBody.put(ParameterConstants.STAFF_ID, "Staff1");
        extractor = new StaffProgramAssociationExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.STAFF_PROGRAM_ASSOCIATION);
        staffToLeaCache = new EntityToEdOrgDateCache();
        staffToLeaCache.addEntry("Staff1", "LEA", DateTime.parse("2011-05-01", DateHelper.getDateTimeFormat()));
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testExtractOneEntity() {
        entityBody.put(ParameterConstants.BEGIN_DATE, "2010-05-01");

        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(staffToLeaCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION));
    }
    
    @Test
    public void testExtractManyEntity() {
        entityBody.put(ParameterConstants.BEGIN_DATE, "2010-05-01");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(staffToLeaCache);
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION));
    }

    @Test
    public void testExtractNoEntityBecauseWrongDate() {
        entityBody.put(ParameterConstants.BEGIN_DATE, "2012-05-01");

        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(staffToLeaCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION));
    }
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        entityBody.put(ParameterConstants.BEGIN_DATE, "2010-05-01");
        Mockito.when(mockEntity.getEntityId()).thenReturn("Staff2");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION), 
                Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        extractor.extractEntities(staffToLeaCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_PROGRAM_ASSOCIATION));
    }
}
