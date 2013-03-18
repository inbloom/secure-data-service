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
package org.slc.sli.bulk.extract;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author tke
 *
 */
public class BulkExtractMongoDATest {

    private MongoEntityRepository mongoEntityRepository;

    @Before
    public void init() throws IOException {
        mongoEntityRepository = Mockito.mock(MongoEntityRepository.class);
    }

    @Ignore
    @Test
    public void testNewTenant() {
        String existingTenant = "Midgar";

        BulkExtractMongoDA bulkExtractMongoDA = new BulkExtractMongoDA();
        bulkExtractMongoDA.setEntityRepository(mongoEntityRepository);

        final String tenantId = "tenantID";
        final String path = "/test/bulkExtract";


        NeutralQuery query = new NeutralQuery(new NeutralCriteria(tenantId, "=", existingTenant));

        Mockito.when(mongoEntityRepository.findOne(Matchers.eq(tenantId), Matchers.eq(query))).thenReturn(null);
        bulkExtractMongoDA.updateDBRecord(tenantId, path, new Date());

        Mockito.verify(mongoEntityRepository, Mockito.atLeastOnce()).create(Matchers.eq(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION), Matchers.anyMap());

    }

    @Test
    public void testExistingTenant() {

        BulkExtractMongoDA bulkExtractMongoDA = new BulkExtractMongoDA();
        bulkExtractMongoDA.setEntityRepository(mongoEntityRepository);

        final String tenantId = "tenantID";
        final String path = "/test/bulkExtract";


        List<Entity> students = TestUtils.createStudents();

        Mockito.when(mongoEntityRepository.findOne(Matchers.eq(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION), Matchers.any(NeutralQuery.class))).thenReturn(students.get(0));
        bulkExtractMongoDA.updateDBRecord(tenantId, path, new Date());

        Mockito.verify(mongoEntityRepository, Mockito.atLeastOnce()).doUpdate(Matchers.eq(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION), Matchers.any(NeutralQuery.class), Matchers.any(Update.class));

    }

}
