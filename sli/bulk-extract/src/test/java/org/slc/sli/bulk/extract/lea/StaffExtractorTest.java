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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


public class StaffExtractorTest {

    private StaffExtractor extractor;

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
    private EntityToEdOrgDateCache staffToLeaDateCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        entityBody = new HashMap<String, Object>();
        extractor = new StaffExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        staffToLeaDateCache = new EntityToEdOrgDateCache();
        staffToLeaDateCache.addEntry("Staff1", "LEA", DateTime.now());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Mockito.when(mockEntity.getEntityId()).thenReturn("Staff1");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExtractOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF));
    }

    @Test
    public void testExtractManyEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF));
    }


    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockEntity.getEntityId()).thenReturn("Staff2");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        extractor.extractEntities(staffToLeaDateCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF));
    }

}
