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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
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
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


public class StaffEdorgAssignmentExtractorTest {

    private StaffEdorgAssignmentExtractor extractor;

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

    @Mock
    private DateHelper mockDateHelper;

    private ExtractorHelper extractorHelper;

    private Map<String, Object> entityBody;
    private EntityToEdOrgCache edorgToLeaCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        entityBody = new HashMap<String, Object>();
        extractorHelper = new ExtractorHelper(mockEdOrgExtractHelper);
        extractorHelper.setDateHelper(mockDateHelper);

        Mockito.when(mockEntity.getBody()).thenReturn(entityBody);
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.STAFF_ED_ORG_ASSOCIATION);

        edorgToLeaCache = new EntityToEdOrgCache();
        edorgToLeaCache.addEntry("LEA", "School1");
        edorgToLeaCache.addEntry("LEA", "School2");

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
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Map<String, List<String>> edOrgLineages = new HashMap<String, List<String>>();
        edOrgLineages.put("School1", Arrays.asList("LEA", "School1"));
        edOrgLineages.put("LEA", Arrays.asList("LEA"));
        Mockito.when(mockEdOrgExtractHelper.getEdOrgLineages()).thenReturn(edOrgLineages);
        Mockito.when(mockDateHelper.getDate(Mockito.eq(entityBody), Mockito.anyString())).thenReturn(DateTime.parse("1941-12-07", DateHelper.getDateTimeFormat()));
        entityBody.put(ParameterConstants.STAFF_REFERENCE, "PHarbor");
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        entityBody.put(ParameterConstants.BEGIN_DATE, "1941-12-07");
        StaffEdorgAssignmentExtractor realExtractor = new StaffEdorgAssignmentExtractor(mockExtractor, mockMap, mockRepo,
                extractorHelper, mockEdOrgExtractHelper);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

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
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Map<String, List<String>> edOrgLineages = new HashMap<String, List<String>>();
        edOrgLineages.put("School1", Arrays.asList("LEA", "School1"));
        edOrgLineages.put("LEA", Arrays.asList("LEA"));
        Mockito.when(mockEdOrgExtractHelper.getEdOrgLineages()).thenReturn(edOrgLineages);
        Mockito.when(mockDateHelper.getDate(Mockito.eq(entityBody), Mockito.anyString())).thenReturn(DateTime.parse("1776-07-04", DateHelper.getDateTimeFormat()));
        entityBody.put(ParameterConstants.STAFF_REFERENCE, "TJefferson");
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        entityBody.put(ParameterConstants.BEGIN_DATE, "1776-07-04");
        StaffEdorgAssignmentExtractor realExtractor = new StaffEdorgAssignmentExtractor(mockExtractor, mockMap, mockRepo,
                extractorHelper, mockEdOrgExtractHelper);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(edorgToLeaCache);

        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

    @Test
    public void testExtractNoEntityBecauseOfExpiration() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(mockFile);
        Map<String, List<String>> edOrgLineages = new HashMap<String, List<String>>();
        edOrgLineages.put("School1", Arrays.asList("LEA", "School1"));
        edOrgLineages.put("LEA", Arrays.asList("LEA"));
        Mockito.when(mockEdOrgExtractHelper.getEdOrgLineages()).thenReturn(edOrgLineages);
        Mockito.when(mockDateHelper.getDate(Mockito.eq(entityBody), Mockito.anyString())).thenReturn(DateTime.parse("1927-02-14", DateHelper.getDateTimeFormat()));
        entityBody.put(ParameterConstants.STAFF_REFERENCE, "ACapone");
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        entityBody.put(ParameterConstants.BEGIN_DATE, "1927-02-14");
        StaffEdorgAssignmentExtractor realExtractor = new StaffEdorgAssignmentExtractor(mockExtractor, mockMap, mockRepo,
                extractorHelper, mockEdOrgExtractHelper);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(false).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(edorgToLeaCache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

    @Test
    public void testExtractNoEntityBecauseOfLEAMiss() {
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity, mockEntity).iterator());
        Mockito.when(mockMap.getExtractFileForEdOrg("LEA")).thenReturn(null);
        Map<String, List<String>> edOrgLineages = new HashMap<String, List<String>>();
        edOrgLineages.put("School1", Arrays.asList("LEA", "School1"));
        edOrgLineages.put("LEA", Arrays.asList("LEA"));
        Mockito.when(mockEdOrgExtractHelper.getEdOrgLineages()).thenReturn(edOrgLineages);
        Mockito.when(mockDateHelper.getDate(Mockito.eq(entityBody), Mockito.anyString())).thenReturn(DateTime.parse("1847-02-11", DateHelper.getDateTimeFormat()));
        entityBody.put(ParameterConstants.STAFF_REFERENCE, "TEdison");
        entityBody.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "School1");
        entityBody.put(ParameterConstants.BEGIN_DATE, "1847-02-11");
        StaffEdorgAssignmentExtractor realExtractor = new StaffEdorgAssignmentExtractor(mockExtractor, mockMap, mockRepo,
                extractorHelper, mockEdOrgExtractHelper);
        extractor = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(extractor).shouldExtract(Mockito.eq(mockEntity), Mockito.any(DateTime.class));

        extractor.extractEntities(edorgToLeaCache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.eq(mockFile),
                Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }

}
