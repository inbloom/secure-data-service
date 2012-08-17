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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(mockedGoodMongoTemplate.findById(any(Object.class), eq(MongoEntity.class), eq("student"))).thenReturn(
                new MongoEntity("Good!", null));
        retryMongoCommand = new RetryMongoCommand(mockedGoodMongoTemplate, mockedLogger);
        assertEquals(1, retryMongoCommand.insertWithRetries(student, "student"));
    }

    @Test
    public void testRetryMongoTemplateCommandWithBad() {
        when(mockedBadMongoTemplate.findById(any(Object.class), eq(MongoEntity.class), eq("student"))).thenThrow(
                new MongoException("Bad!"));
        retryMongoCommand = new RetryMongoCommand(mockedBadMongoTemplate, mockedLogger);
        assertEquals(0, retryMongoCommand.findOneWithRetries(new Query(), MongoEntity.class, "student"));
    }

    @Test
    public void testRetryMongoTemplateCommandWithNotSoBad() {
        when(mockedNotSoBadMongoTemplate.findById(any(Object.class), eq(MongoEntity.class), eq("student"))).thenThrow(
                new MongoException("Bad!")).thenReturn(new MongoEntity("Good!", null));
        retryMongoCommand = new RetryMongoCommand(mockedNotSoBadMongoTemplate, mockedLogger);
        assertEquals(2, retryMongoCommand.findOneWithRetries(new Query(), MongoEntity.class, "student"));
    }

    @Test
    public void testRetryMongoTemplateCommandWithDuplicate() {
        doThrow(new MongoException("Duplicate!")).when(mockedDuplicateMongoTemplate).insert(any(Object.class),
                eq("student"));
        retryMongoCommand = new RetryMongoCommand(mockedDuplicateMongoTemplate, mockedLogger);
        assertEquals(1, retryMongoCommand.insertWithRetries(student, "student"));
    }
}
