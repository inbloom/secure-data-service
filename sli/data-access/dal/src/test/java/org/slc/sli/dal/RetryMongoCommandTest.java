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

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Junit for RetryMongoCommand
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RetryMongoCommandTest {

    private MongoTemplate mockedGoodMongoTemplate;
    private MongoTemplate mockedBadMongoTemplate;
    private MongoTemplate mockedDuplicateMongoTemplate;

    @Before
    public void setup() {
        // Setup the mocked Mongo Template.
        mockedGoodMongoTemplate = mock(MongoTemplate.class);
        mockedBadMongoTemplate = mock(MongoTemplate.class);
        mockedDuplicateMongoTemplate = mock(MongoTemplate.class);
    }

    @Test
    public void testRetryMongoTemplateCommandWithGood() {
    }

    @Test
    public void testRetryMongoTemplateCommandWithBad() {
    }

    @Test
    public void testRetryMongoTemplateCommandWithDuplicate() {
    }
}
