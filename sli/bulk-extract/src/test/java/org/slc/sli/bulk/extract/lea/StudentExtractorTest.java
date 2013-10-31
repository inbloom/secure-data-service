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

import junit.framework.Assert;

import com.google.common.base.Predicate;

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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


public class StudentExtractorTest {

    private StudentExtractor extractor;

    @Mock
    private Repository<Entity> mockRepo;

    @Mock
    private EntityExtractor mockExtractor;

    @Mock
    private ExtractFileMap mockMap;

    @Mock
    private ExtractorHelper helper;

    @Mock
    private EdOrgExtractHelper edOrgExtractHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockMap.getEdOrgs()).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
        extractor = new StudentExtractor(mockExtractor, mockMap, mockRepo, helper, edOrgExtractHelper);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testOneExtractedEntity() {
        Entity e = Mockito.mock(Entity.class);
        Mockito.when(mockRepo.findEach(Mockito.eq("student"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e).iterator());
        Map<String, DateTime> datedEdorgs = new HashMap<String, DateTime>();
        datedEdorgs.put("LEA", DateTime.now());
        Mockito.when(helper.fetchAllEdOrgsForStudent(Mockito.any(Entity.class))).thenReturn(datedEdorgs);

        extractor.extractEntities(null);

        Mockito.verify(mockExtractor).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("student"), Mockito.any(Predicate.class));

    }

    @Test
    public void testOneExtractedEntityUpdatesCache() {
        Entity e = Mockito.mock(Entity.class);
        Mockito.when(mockRepo.findEach(Mockito.eq("student"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(e).iterator());
        Map<String, DateTime> datedEdorgs = new HashMap<String, DateTime>();
        datedEdorgs.put("LEA", DateTime.now());
        Mockito.when(helper.fetchAllEdOrgsForStudent(Mockito.any(Entity.class))).thenReturn(datedEdorgs);
        Mockito.when(helper.fetchCurrentParentsFromStudent(Mockito.any(Entity.class))).thenReturn(
                new HashSet<String>(Arrays.asList("Mommy", "Daddy")));

        extractor.extractEntities(null);

        Mockito.verify(mockExtractor).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("student"), Mockito.any(Predicate.class));
        Assert.assertEquals(2, extractor.getParentCache().getEntityIds().size());
        Assert.assertEquals(1, extractor.getParentCache().getEntriesById("Mommy").size());
        Assert.assertTrue(extractor.getParentCache().getEntriesById("Mommy").contains("LEA"));
        Assert.assertEquals(1, extractor.getParentCache().getEntriesById("Daddy").size());
        Assert.assertTrue(extractor.getParentCache().getEntriesById("Daddy").contains("LEA"));
    }

    @Test
    public void testManyExtractedEntities() {
        Entity e = Mockito.mock(Entity.class);
        Mockito.when(mockRepo.findEach(Mockito.eq("student"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(e, e, e).iterator());
        Map<String, DateTime> datedEdorgs = new HashMap<String, DateTime>();
        datedEdorgs.put("LEA", DateTime.now());
        Mockito.when(helper.fetchAllEdOrgsForStudent(Mockito.any(Entity.class))).thenReturn(datedEdorgs);

        extractor.extractEntities(null);

        Mockito.verify(mockExtractor, Mockito.times(3)).extractEntity(Mockito.any(Entity.class),
                Mockito.any(ExtractFile.class), Mockito.eq("student"), Mockito.any(Predicate.class));
    }

    @Test
    public void testNoExtractedEntities() {
        // Cache LEA != Student Edorgs
        Entity e = Mockito.mock(Entity.class);
        Mockito.when(mockRepo.findEach(Mockito.eq("student"), Mockito.eq(new NeutralQuery()))).thenReturn(
                Arrays.asList(e).iterator());

        extractor.extractEntities(null);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.any(Entity.class),
                Mockito.any(ExtractFile.class), Mockito.eq("student"));

        // No Edorgs
        extractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.any(Entity.class),
                Mockito.any(ExtractFile.class), Mockito.eq("student"));
    }

}
