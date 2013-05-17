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
package org.slc.sli.bulk.extract.aspect;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.aspect.LoggerCarrierAspect;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.dal.template.MongoEntityTemplate;
import org.slc.sli.domain.Entity;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Test class for LoggerCarrierAspect.
 * @author npandey
 */
public class LoggerCarrierAspectTest {

    private MongoRepository<Entity> mockedEntityRepository;

    /**
     * Initialization method for the test class.
     */
    @Before
    public void init() {
        mockedEntityRepository = mock(MongoRepository.class);
        LoggerCarrierAspect.aspectOf().setEntityRepository(mockedEntityRepository);
    }

    /**
     * Test the audit method
     */
    @Test
    public void testAudit() {
       audit(SecurityEventUtil.createSecurityEvent(this.getClass().getName(), "TestMessage", "Action", LogLevelType.TYPE_TRACE));
       Mockito.verify(mockedEntityRepository, times(1)).create(any(String.class), any(Map.class), any(Map.class), any(String.class));
    }
}
