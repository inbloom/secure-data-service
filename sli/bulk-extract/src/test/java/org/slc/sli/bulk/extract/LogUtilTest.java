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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.slc.sli.bulk.extract.LogUtil.audit;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.Entity;

/**
 * Test class for LoggerCarrierAspect.
 * @author npandey
 */
public class LogUtilTest {

    private MongoRepository<Entity> mockedEntityRepository;

    /**
     * Initialization method for the test class.
     */
    @Before
    public void init() {
        mockedEntityRepository = mock(MongoRepository.class);
        LogUtil.setEntityRepository(mockedEntityRepository);
    }

    /**
     * Test the audit method
     */
    @Test
    public void testAudit() {
       SecurityEvent securityEvent = new SecurityEvent();
       securityEvent.setClassName(this.getClass().getName());
       securityEvent.setLogMessage("Test Message");
       securityEvent.setLogLevel(LogLevelType.TYPE_TRACE);

       audit(securityEvent);
       Mockito.verify(mockedEntityRepository, times(1)).create(any(String.class), any(Map.class), any(Map.class), any(String.class));
    }
}
