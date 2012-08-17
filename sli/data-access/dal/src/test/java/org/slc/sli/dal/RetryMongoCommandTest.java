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

package org.slc.sli.dal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import com.mongodb.MongoException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.MongoEntity;

/**
 * Junit for RetryMongoCommand
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RetryMongoCommandTest {

    private RetryMongoCommand retryMongoCommand = null;

    private MongoTemplate mockedGoodMongoTemplate;
    private MongoTemplate mockedBadMongoTemplate;
    private MongoTemplate mockedNotSoBadMongoTemplate;
    private MongoTemplate mockedDuplicateMongoTemplate;
    private Logger mockedLogger;
    MongoEntity student;

    @Before
    public void setup() {
        // Setup the mocked Mongo Templates and Logger.
        mockedGoodMongoTemplate = mock(MongoTemplate.class);
        mockedBadMongoTemplate = mock(MongoTemplate.class);
        mockedNotSoBadMongoTemplate = mock(MongoTemplate.class);
        mockedDuplicateMongoTemplate = mock(MongoTemplate.class);
        mockedLogger = mock(Logger.class);
        student = new MongoEntity("student", null);
    }

    @Test
    public void testRetryMongoTemplateCommandWithGood() {
        MongoEntity expected = new MongoEntity("student", null);
        when(mockedGoodMongoTemplate.findOne(any(Query.class), eq(MongoEntity.class), eq("student"))).thenReturn(
                expected);
        retryMongoCommand = new RetryMongoCommand(mockedGoodMongoTemplate, mockedLogger);
        int tries = 0;
        try {
            MongoEntity found = retryMongoCommand.findOneWithRetries(new Query(), MongoEntity.class, "student", tries);
            assertEquals(expected, found);
            assertEquals(1, tries);
        } catch (MongoException me) {
            fail();
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithBad() {
        when(mockedBadMongoTemplate.findOne(any(Query.class), eq(MongoEntity.class), eq("student"))).thenThrow(
                new MongoException("Bad!"));
        retryMongoCommand = new RetryMongoCommand(mockedBadMongoTemplate, mockedLogger);
        MongoEntity found = null;
        int tries = 0;
        try {
            found = retryMongoCommand.findOneWithRetries(new Query(), MongoEntity.class, "student", tries);
            fail();
        } catch (MongoException me) {
            assertEquals(0, tries);
            Assert.assertNull(found);
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithNotSoBad() {
        MongoEntity expected = new MongoEntity("student", null);
        when(mockedNotSoBadMongoTemplate.findOne(any(Query.class), eq(MongoEntity.class), eq("student"))).thenThrow(
                new MongoException("Bad!")).thenReturn(expected);
        retryMongoCommand = new RetryMongoCommand(mockedNotSoBadMongoTemplate, mockedLogger);
        int tries = 0;
        try {
            MongoEntity found = retryMongoCommand.findOneWithRetries(new Query(), MongoEntity.class, "student", tries);
            assertEquals(expected, found);
            assertEquals(2, tries);
        } catch (MongoException me) {
            fail();
        }
    }

    @Test
    public void testRetryMongoTemplateCommandWithDuplicate() {
        doThrow(new MongoException("Duplicate!")).when(mockedDuplicateMongoTemplate).insert(any(Object.class),
                eq("student"));
        retryMongoCommand = new RetryMongoCommand(mockedDuplicateMongoTemplate, mockedLogger);
        int tries = 0;
        try {
            retryMongoCommand.insertWithRetries(student, "student", tries);
            assertEquals(1, tries);
        } catch (MongoException me) {
            fail();
        }
    }
}
