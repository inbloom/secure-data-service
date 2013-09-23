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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.*;


public class StaffEdorgAssignmentExtractorTest {
    private StaffEdorgAssignmentExtractor extractor;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private ExtractorHelper mockExtractorHelper;
    @Mock
    private EntityToEdOrgCache mockCache;
    @Mock
    private Entity mockEntity;
    @Mock
    private ExtractFile mockFile;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    private Map<String, Object> entityBody;
    private EntityToEdOrgCache edorgToLeaCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        entityBody = new HashMap<String, Object>();
        extractor = new StaffEdorgAssignmentExtractor(mockExtractor, mockMap, mockRepo, mockExtractorHelper, mockCache, mockEdOrgExtractHelper);
        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        edorgToLeaCache = new EntityToEdOrgCache();
        edorgToLeaCache.addEntry("LEA", "School1");
        edorgToLeaCache.addEntry("LEA", "School2");

        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Mockito.when(mockCache.getEntriesById(null)).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testExtractOneEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery()))).thenAnswer(new Answer<Iterator<Entity>>() {
            @Override
            public Iterator<Entity> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(mockEntity).listIterator(0);
            }
        });
        Mockito.when(mockExtractorHelper.isStaffAssociationCurrent(mockEntity)).thenReturn(true);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        extractor.extractEntities(edorgToLeaCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile), Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

    @Test
    public void testExtractManyEntity() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenAnswer(new Answer<Iterator<Entity>>() {
                    @Override
                    public Iterator<Entity> answer(InvocationOnMock invocation) throws Throwable {
                        return Arrays.asList(mockEntity, mockEntity).listIterator(0);
                    }
                });
        Mockito.when(mockExtractorHelper.isStaffAssociationCurrent(mockEntity)).thenReturn(true);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        extractor.extractEntities(edorgToLeaCache);
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

    @Test
    public void testExtractNoEntityBecauseOfExpiration() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockExtractorHelper.isStaffAssociationCurrent(mockEntity)).thenReturn(false);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        extractor.extractEntities(edorgToLeaCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockExtractorHelper.isStaffAssociationCurrent(mockEntity)).thenReturn(false);
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        extractor.extractEntities(edorgToLeaCache);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }
}
