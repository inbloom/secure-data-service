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
package org.slc.sli.bulk.extract.extractor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.context.resolver.TypeResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.EducationOrganizationContextResolver;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;
import org.slc.sli.bulk.extract.files.EntityWriterManager;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

public class DeltaExtractorTest {

    @InjectMocks
    DeltaExtractor extractor = new DeltaExtractor();

    @Mock
    DeltaEntityIterator deltaEntityIterator;

    @Mock
    LocalEdOrgExtractor leaExtractor;

    @Mock
    LocalEdOrgExtractHelper helper;

    @Mock
    EntityExtractor entityExtractor;

    @Mock
    BulkExtractMongoDA bulkExtractMongoDA;

    @Mock
    EntityWriterManager entityWriteManager;

    @Mock
    TypeResolver typeResolver;

    @Mock
    EdOrgHierarchyHelper edOrgHelper;

    @Mock
    EducationOrganizationContextResolver edorgContextResolver;

    @Mock
    Repository<Entity> repo;

    @Mock
    ExtractFile extractFile;

    @Mock
    ManifestFile metaDataFile;

    @Mock
    SecurityEventUtil securityEventUtil;

    // There are two top level LEAs that have apps authorized, lea1 and lea2.
    // app1 is authorized for both lea1 and lea2
    // app2 is authorized for lea2 only

    // two delta records
    // first is an update event in lea1 with a spam delete request
    // --> lea1 should generate an update event (with both apps)
    // --> lea2 should generate an delete event
    // 1 write calls and 1 writeDelete call

    // second is an delete event in lea2
    // --> lea1 should generate an delete event
    // --> lea2 should generate an delete event
    // 2 writeDelete calls

    // 3 writeDeletes <--> however since educationOrganization contains both
    // school and educationOrganization, we must spam delete in both collections
    // which results 6 writeDelete calls

    @Before
    public void setUp() throws Exception {
        deltaEntityIterator = Mockito.mock(DeltaEntityIterator.class);
        leaExtractor = Mockito.mock(LocalEdOrgExtractor.class);
        entityExtractor = Mockito.mock(EntityExtractor.class);
        bulkExtractMongoDA = Mockito.mock(BulkExtractMongoDA.class);
        entityWriteManager = Mockito.mock(EntityWriterManager.class);
        typeResolver = Mockito.mock(TypeResolver.class);
        edorgContextResolver = Mockito.mock(EducationOrganizationContextResolver.class);
        repo = Mockito.mock(MongoEntityRepository.class);
        extractFile = Mockito.mock(ExtractFile.class);
        metaDataFile = Mockito.mock(ManifestFile.class);
        securityEventUtil = Mockito.mock(SecurityEventUtil.class);
        edOrgHelper = Mockito.mock( EdOrgHierarchyHelper.class);

        MockitoAnnotations.initMocks(this);

        Map<String, Set<String>> appsToLEA = buildAppToLEAMap();
        when(helper.getBulkExtractEdOrgsPerApp()).thenReturn(appsToLEA);
        Entity LEA1 = buildEdorgEntity("lea1");
        Entity LEA2 = buildEdorgEntity("lea2");
        Entity LEA3 = buildEdorgEntity("lea3");
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "lea1")).thenReturn(LEA1);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "lea2")).thenReturn(LEA2);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "lea3")).thenReturn(LEA3);

        when(edorgContextResolver.findGoverningEdOrgs(LEA1)).thenReturn(new HashSet<String>(Arrays.asList("lea1")));
        when(edorgContextResolver.findGoverningEdOrgs(LEA2)).thenReturn(new HashSet<String>(Arrays.asList("lea2")));
        when(edorgContextResolver.findGoverningEdOrgs(LEA3)).thenReturn(new HashSet<String>(Arrays.asList("lea1")));

        when(deltaEntityIterator.hasNext()).thenReturn(true, true, false);
        when(deltaEntityIterator.next()).thenReturn(buildUpdateRecord(), buildDeleteRecord());


        when(typeResolver.resolveType("educationOrganization")).thenReturn(new HashSet<String>(Arrays.asList("school", "educationOrganization")));
        when(extractFile.getManifestFile()).thenReturn(metaDataFile);
        SecurityEvent event = new SecurityEvent();
        event.setLogLevel(LogLevelType.TYPE_INFO);
        when(securityEventUtil.createSecurityEvent(anyString(), anyString(), any(LogLevelType.class), any(BEMessageCode.class), anyString())).thenReturn(event);
        when(extractor.isSea("lea1")).thenReturn(false);
        when(extractor.isSea("lea2")).thenReturn(false);
        when(extractor.isSea("lea3")).thenReturn(false);

        // when(leaExtractor.getExtractFilePerAppPerLEA(anyString(), anyString(), anyString(),
        // any(DateTime.class), anyBoolean())).thenReturn(extractFile);

    }

    private DeltaRecord buildUpdateRecord() {
        DeltaEntityIterator.DeltaRecord update = new DeltaEntityIterator.DeltaRecord(buildEdorgEntity("lea1"),
                new HashSet<String>(Arrays.asList("lea1")), DeltaEntityIterator.Operation.UPDATE, true, "educationOrganization");
        return update;
    }

    private DeltaRecord buildPurgeRecord() {
        MongoEntity purge = new MongoEntity("purge", null, new HashMap<String, Object>(), null);
        DeltaEntityIterator.DeltaRecord res = new DeltaEntityIterator.DeltaRecord(purge,
               null, DeltaEntityIterator.Operation.PURGE, false, "purge");
        return res;
    }

    private DeltaRecord buildDeleteRecord() {
        DeltaEntityIterator.DeltaRecord delete = new DeltaEntityIterator.DeltaRecord(buildEdorgEntity("lea2"),
                new HashSet<String>(Arrays.asList("lea2")), DeltaEntityIterator.Operation.DELETE, false, "educationOrganization");
        return delete;
    }

    private Entity buildEdorgEntity(String id) {
        return new MongoEntity("educationOrganization", id, new HashMap<String, Object>(), null);
    }

    private Map<String, Set<String>> buildAppToLEAMap() {
        Set<String> app1 = new HashSet<String>(Arrays.asList("lea1", "lea2"));
        Set<String> app2 = new HashSet<String>(Arrays.asList("lea2", "lea3"));
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        result.put("app1", app1);
        result.put("app2", app2);

        return result;
    }

    @Test
    public void test() {
        extractor.execute("Midgar", new DateTime(), "");
        verify(entityExtractor, times(1)).write(any(Entity.class), any(ExtractFile.class), any(EntityExtractor.CollectionWrittenRecord.class), (Predicate) Mockito.isNull());
        verify(entityWriteManager, times(6)).writeDeleteFile(any(Entity.class), any(ExtractFile.class));
    }

    @Test
    public void testPurge() {
        when(deltaEntityIterator.hasNext()).thenReturn(true, false);
        when(deltaEntityIterator.next()).thenReturn(buildPurgeRecord());

        extractor.execute("Midgar", new DateTime(), "");

        verify(entityWriteManager, times(2)).writeDeleteFile(any(Entity.class), any(ExtractFile.class));
    }

}
