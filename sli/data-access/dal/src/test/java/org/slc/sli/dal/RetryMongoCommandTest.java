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

package org.slc.sli.dal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNull;

import com.mongodb.MongoException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Junit for RetryMongoCommand
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RetryMongoCommandTest {

    private static final int MONGO_DUPLICATE_KEY_CODE_1 = 11000;
    MongoEntity student;
    int count;

    @Before
    public void setup() {
        // Setup the expected findOne result..
        student = new MongoEntity("student", null);
    }

    @Test
    public void testRetryMongoTemplateCommandWithGood() {
        final MongoEntity expected = student;
        int tries = 3;
        count = 0;
        try {
            MongoEntity found = findOneWithRetries("good_student", new NeutralQuery(), tries);
            assertEquals(expected, found);
            assertEquals(1, count);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithBad() {
        MongoEntity found = null;
        int tries = 3;
        count = 0;
        try {
            found = findOneWithRetries("bad_student", new NeutralQuery(), tries);
            fail();
        } catch (Exception ex) {
            assertEquals(3, count);
            assertNull(found);
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithNotSoBad() {
        MongoEntity expected = student;
        int tries = 3;
        count = 0;
        try {
            MongoEntity found = findOneWithRetries("not_so_bad_student", new NeutralQuery(), tries);
            assertEquals(expected, found);
            assertEquals(2, count);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithDuplicate() {
        int tries = 3;
        count = 0;
        Entity record = null;
        try {
            record = createWithRetries(student, "student", tries);
            fail();
        } catch (Exception ex) {
            assertEquals(1, count);
            assertNull(record);
        }
    }

    private MongoEntity findOne(final String collectionName, final NeutralQuery neutralQuery) {
        count++;
        if (collectionName.equals("good_student")) {
            return student;
        } else if (collectionName.equals("not_so_bad_student")) {
            if (count == 2) {
                return student;
            } else {
                throw new MongoException("Cannot find record");
            }
        } else {
            throw new MongoException("Cannot find record");
        }
    }

    private MongoEntity findOneWithRetries(final String collectionName, final NeutralQuery neutralQuery, int retries)
            throws Exception {
        RetryMongoCommand retryMongoCommand = new RetryMongoCommand() {
            @Override
            public Object execute() {
                return findOne(collectionName, neutralQuery);
            }
        };
        try {
            return (MongoEntity) retryMongoCommand.executeOperation(retries);
        } catch (Exception e) {
            throw (e);
        }
    }

    public Entity create(final Entity record, final String collectionName) {
        count++;
        MongoException me = Mockito.mock(MongoException.DuplicateKey.class);
        throw me;
    }

    private Entity createWithRetries(final Entity record, final String collectionName, int retries) throws Exception {
        RetryMongoCommand retryMongoCommand = new RetryMongoCommand() {
            @Override
            public Object execute() {
                return create(record, collectionName);
            }
        };
        try {
            return (MongoEntity) retryMongoCommand.executeOperation(retries);
        } catch (Exception e) {
            throw (e);
        }
    }
}
