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
package org.slc.sli.bulk.extract;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import org.slc.sli.dal.repository.MongoEntityRepository;

/** UT for bulkExractMongoDA.
 * @author tke
 *
 */
public class BulkExtractMongoDATest {

    private MongoEntityRepository mongoEntityRepository;

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {
        mongoEntityRepository = Mockito.mock(MongoEntityRepository.class);
    }

    /**
     * Test that the bulk extract collection is updated.
     */
    @Test
    public void testTenant() {

        BulkExtractMongoDA bulkExtractMongoDA = new BulkExtractMongoDA();
        bulkExtractMongoDA.setEntityRepository(mongoEntityRepository);

        final String tenantId = "tenantID";
        final String path = "/test/bulkExtract";
        final String app = "testApp";

        bulkExtractMongoDA.updateDBRecord(tenantId, path, app, new Date(), false, null, false);

        Mockito.verify(mongoEntityRepository, Mockito.times(1)).update(Matchers.eq(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION), Matchers.any(BulkExtractEntity.class), Matchers.eq(false));

    }


}
