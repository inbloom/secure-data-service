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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class GraduationPlanExtractorTest {
    private GraduationPlanExtractor extractor;
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
    private EntityToEdOrgCache mockGraduationPlanCache;
    
    @Mock
    private Entity mockEntity;
    private Map<String, Object> entityBody;
    
    @Mock
    private Entity mockTransitiveEntity;
    private Map<String, Object> transitiveEntityBody;

    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extractor = new GraduationPlanExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
        
        entityBody = new HashMap<String, Object>();
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getEntityId()).thenReturn("graduationPlanId1");
        transitiveEntityBody = new HashMap<String, Object>();
        transitiveEntityBody.put("educationOrganizationId", "edorgId2");
        Mockito.when(mockTransitiveEntity.getBody()).thenReturn(transitiveEntityBody);
        Mockito.when(mockTransitiveEntity.getEntityId()).thenReturn("graduationPlanId2");
        
        mockEdorgCache = new EntityToEdOrgCache();
        mockEdorgCache.addEntry("LEA", "edorgId1");
        mockEdorgCache.addEntry("LEA", "edorgId2");
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        
    }
    
    @Test
    public void testWriteOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        entityBody.put("educationOrganizationId", "edorgId1");
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }
    
    @Test
    public void testWriteManyEntities() {
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity, mockEntity).iterator());
        entityBody.put("educationOrganizationId", "edorgId1");
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        entityBody.put("educationOrganizationId", "edorgId1");
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }
    
    @Test
    public void testExtractNoEntityBecauseOfIdMiss() {
        entityBody.put("educationOrganizationId", "missId");
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA-1")).thenReturn(mockFile);
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }
    
    @Test
    public void testTransitiveNoDupes() {
        entityBody.put("educationOrganizationId", "edorgId1");;
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
        .thenReturn(Arrays.asList(mockEntity).iterator());
        Mockito.when(mockGraduationPlanCache.getEntriesById("graduationPlanId1"))
                .thenReturn(new HashSet<String>(Arrays.asList("LEA")));

        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }

    @Test
    public void testTransitive() {
        entityBody.put("educationOrganizationId", "edorgId1");
        transitiveEntityBody.put("educationOrganizationId", "edorgId3");
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity, mockTransitiveEntity).iterator());
        
        Mockito.when(mockGraduationPlanCache.getEntriesById("graduationPlanId2"))
                .thenReturn(new HashSet<String>(Arrays.asList("LEA")));
       
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockTransitiveEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }
    
    @Test
    public void testTransitiveNoMatch() {
        entityBody.put("educationOrganizationId", "edorgId1");;
        transitiveEntityBody.put("educationOrganizationId", "edorgId3");
        Mockito.when(mockRepo.findEach(Mockito.eq("graduationPlan"), Mockito.eq(new Query())))
                .thenReturn(Arrays.asList(mockEntity, mockTransitiveEntity).iterator());
        
        Mockito.when(mockGraduationPlanCache.getEntriesById("graduationPlanId2"))
                .thenReturn(new HashSet<String>(Arrays.asList("edorgId3")));
        extractor.extractEntities(mockEdorgCache, mockGraduationPlanCache);
        
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
        Mockito.verify(mockExtractor, Mockito.times(0)).extractEntity(Mockito.eq(mockTransitiveEntity), Mockito.eq(mockFile),
                Mockito.eq("graduationPlan"));
    }

}
