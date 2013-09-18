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
import java.util.HashSet;

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

public class ParentExtractorTest {
    private ParentExtractor extractor;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFile mockFile;
    
    @Mock
    private EntityToEdOrgCache mockParentCache;
    
    @Mock
    private Entity mockEntity;

    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extractor = new ParentExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        Mockito.when(mockEntity.getEntityId()).thenReturn("parent");
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Mockito.when(mockParentCache.getEntriesById("parent")).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
        Mockito.when(mockParentCache.getEntityIds()).thenReturn(new HashSet<String>(Arrays.asList("parent")));
    }
    
    @Test
    public void testWriteOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.PARENT), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity).iterator());
        extractor.extractEntities(mockParentCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.PARENT));
    }
    
    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.PARENT), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        extractor.extractEntities(mockParentCache);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.PARENT));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.PARENT), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        extractor.extractEntities(mockParentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.PARENT));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        Mockito.when(mockEntity.getEntityId()).thenReturn("parent2");
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.PARENT), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        extractor.extractEntities(mockParentCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.PARENT));
    }
}
