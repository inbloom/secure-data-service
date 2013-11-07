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
package org.slc.sli.bulk.extract.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test for the SecurityEventUtil class.
 * @author npandey
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SecurityEventUtilTest {

     @Autowired
     private SecurityEventUtil securityEventUtil;

    /**
     * Test that the security even object is created as expected.
     */
    @Test
    public void testSEValues() {
        SecurityEventUtil spyObject = Mockito.spy(securityEventUtil);

        SecurityEvent event = spyObject.createSecurityEvent("class", "Action Description", LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0001);
        String appId = null;
        Mockito.verify(spyObject, Mockito.atMost(1)).createSecurityEvent(Mockito.anyString(), Mockito.anyString(), Mockito.eq(LogLevelType.TYPE_INFO), Mockito.eq(appId), Mockito.eq(BEMessageCode.BE_SE_CODE_0001));
        assertEquals("BulkExtract", event.getAppId());
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        assertEquals(processName, event.getProcessNameOrId());
        assertEquals(null, event.getTargetEdOrgList());
    }

    /**
     * Set securityEventUtil.
     * @param securityEventUtil the securityEventUtil to set
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }
}
