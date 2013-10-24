/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.bulk.extract.delta;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.bulk.extract.context.resolver.EdOrgContextResolverFactory;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.Operation;
import org.slc.sli.dal.repository.DeltaJournal;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class DeltaEntityIteratorTest {
    
    private static final String UPDATE_ID = "1234567890123456789012345678901234567890_id";
    private static final String DELETE_ID = "1234567890123456789012345678901234567891_id";
    private static final String PURGE_ID = "1234567890123456789012345678901234567892_id";

    @InjectMocks
    DeltaEntityIterator iterator = new DeltaEntityIterator();

    @Mock
    EdOrgContextResolverFactory resolverFactory;
    
    @Mock
    Repository<Entity> repo;
    
    @Mock
    DeltaJournal deltaJournal;

    ContextResolver testResolver = new ContextResolver() {
        @Override
        public Set<String> findGoverningEdOrgs(Entity entity) {
            List<String> lists = Arrays.asList("LEA1", "LEA2");
            return new HashSet<String>(lists);
        }
        @Override
        public Set<String> findGoverningEdOrgs(String id, Entity entityToExtract) {
            return null;
        }

        @Override
        public Set<String> findGoverningEdOrgs(Entity entity, Entity entityToExtract) {
            List<String> lists = Arrays.asList("LEA1", "LEA2");
            return new HashSet<String>(lists);
        }
    };

    @Before
    public void setup() throws Exception {
        resolverFactory = Mockito.mock(EdOrgContextResolverFactory.class);
        repo = Mockito.mock(MongoEntityRepository.class);
        deltaJournal = Mockito.mock(DeltaJournal.class);
        MockitoAnnotations.initMocks(this);
       
        List<Map<String, Object>> deltaEntities = buildDeltaCollections();
        final Set<String> ids = new HashSet<String>(Arrays.asList(UPDATE_ID));
        List<Entity> edorgList = new ArrayList<Entity>();
        edorgList.add(buildEdorgEntity(UPDATE_ID));
        
        when(deltaJournal.findDeltaRecordBetween(anyLong(), anyLong())).thenReturn(deltaEntities.iterator());
        when(repo.findAll(eq("educationOrganization"), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object item) {
                NeutralQuery matching = DeltaEntityIterator.buildBatchQuery("educationOrganization", ids);
                NeutralQuery other = (NeutralQuery) item;
                return matching.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub
                
            }
        }))).thenReturn(edorgList);
        when(resolverFactory.getResolver(anyString())).thenReturn(testResolver);
    }
    
    private List<Map<String, Object>> buildDeltaCollections() {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        results.add(buildDelta(UPDATE_ID, DeltaEntityIterator.Operation.UPDATE));
        results.add(buildDelta(DELETE_ID, DeltaEntityIterator.Operation.DELETE));
        results.add(buildDelta(PURGE_ID, Operation.PURGE));
        return results;
    }

    private Map<String, Object> buildDelta(String id, Operation op) {
        Map<String, Object> res = new HashMap<String, Object>();
        List<byte[]> byteId = DeltaJournal.getByteId(id);
        res.put("_id", byteId.get(0));
        long now = new DateTime().getMillis();
        if(op != Operation.PURGE) {
            res.put("c", "educationOrganization");
        } else {
            res.put("c", "purge");
        }
        if (op == Operation.DELETE) {
            res.put("d", now);
        } else if (op == Operation.UPDATE) {
            // spamDelete should be set for this one
            res.put("d", now - 1000);
            res.put("u", now);
        }
        res.put("t", now);
        return res;
    }

    private Entity buildEdorgEntity(String id) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("must", "have something");
        body.put("or", "I think you have been deleted");
        Entity edorg = new MongoEntity("educationOrganization", id, body, new HashMap<String, Object>());
        return edorg;
    }

    @Test
    public void deltaCollectionShouldBecomeDeltaRecords() {
        List<String> lists = Arrays.asList("LEA1", "LEA2");
        Set<String> governingLEAs = new HashSet<String>(lists);
        iterator.init("Midgar", new DateTime());
        // should have three delta records
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            DeltaRecord record = iterator.next();
            if (count == 1) {
                // first one is a spam delete update record
                assertEquals(governingLEAs, record.getBelongsToEdOrgs());
                assertTrue(record.isSpamDelete());
                assertEquals(DeltaEntityIterator.Operation.UPDATE, record.getOp());
            } else if (count == 2) {
                assertEquals(null, record.getBelongsToEdOrgs());
                assertFalse(record.isSpamDelete());
                assertEquals(DeltaEntityIterator.Operation.DELETE, record.getOp());
            } else if(count == 3) {
                assertEquals(null, record.getBelongsToEdOrgs());
                assertFalse(record.isSpamDelete());
                assertEquals(Operation.PURGE, record.getOp());
            }
        }
        assertTrue(count == 3);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void cannotRemoveAnyDeltaRecord() {
        iterator.init("Midgar", new DateTime());
        // should have two delta records
        while (iterator.hasNext()) {
            iterator.remove();
        }
    }

}
