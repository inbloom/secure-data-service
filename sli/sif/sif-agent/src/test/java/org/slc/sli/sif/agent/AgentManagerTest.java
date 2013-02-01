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


package org.slc.sli.sif.agent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.Agent;
import openadk.library.ElementDef;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.ZoneFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.subscriber.SifSubscriber;

/**
 * JUnit tests for AgentManager
 *
 * @author jtully
 *
 */
public class AgentManagerTest {
    @InjectMocks
    AgentManager agentManager;

    @Mock
    private SifAgent mockAgent;

    @Mock
    private ZoneFactory mockZoneFactory;

    @Mock
    private Zone mockZone;

    public static final String TEST_ZONE_NAME = "TestZone";
    public static final String SUBSCRIBE_DATA_TYPE = "StudentDTD.StudentPersonal";

    @Before
    public void setup() {
        agentManager = new AgentManager();
        agentManager.setLogPath(AdkTest.ADK_LOG_DIR);
        agentManager.setAdkLogFile(AdkTest.ADK_LOG_FILE);

        MockitoAnnotations.initMocks(this);

        agentManager.setSubscriberZoneName("TestZone");
        List<String> subscribeDataTypes = new ArrayList<String>();
        subscribeDataTypes.add(SUBSCRIBE_DATA_TYPE);
        agentManager.setSubscribeTypeList(subscribeDataTypes);

        Mockito.when(mockAgent.getZoneFactory()).thenReturn(mockZoneFactory);
        Mockito.when(mockZoneFactory.getZone(Mockito.eq(TEST_ZONE_NAME))).thenReturn(mockZone);
    }

    @Test
    public void shouldStartAgentOnSetup() throws Exception {
        agentManager.setup();
        Mockito.verify(mockAgent, Mockito.times(1)).startAgent();
    }

    @Test
    public void shouldSubscribeToZoneOnSetup() throws Exception {
        agentManager.setup();
        Mockito.verify(mockAgent, Mockito.times(1)).startAgent();
        Mockito.verify(mockZone, Mockito.times(1)).setSubscriber(Mockito.any(SifSubscriber.class),
                Mockito.any(ElementDef.class), Mockito.any(SubscriptionOptions.class));

        SifAgent agent = new SifAgent();
        Assert.assertTrue("SifAgent should extend", agent instanceof Agent);
    }

    @Test
    public void shouldShutdownAgentOnCleanup() throws ADKException {
        agentManager.cleanup();
        Mockito.verify(mockAgent, Mockito.times(1)).shutdown(Mockito.eq(ADKFlags.PROV_NONE));
    }
}
