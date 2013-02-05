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

package org.slc.sli.sif.reporting;

import java.util.Properties;

import junit.framework.Assert;
import openadk.library.Agent;
import openadk.library.AgentProperties;
import openadk.library.SIFVersion;
import openadk.library.TransportManager;
import openadk.library.TransportProperties;
import openadk.library.Zone;
import openadk.library.impl.Transport;

import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.sif.EventReporterAdkTest;
import org.slc.sli.sif.zone.ZoneConfigurator;

/**
 * JUnit tests for EventReporterAgent
 *
 */
public class EventReporterAgentTest extends EventReporterAdkTest {
    private EventReporterAgent createEventReporterAgent(ZoneConfigurator zoneConfig) {
        Properties agentProperties = new Properties();
        agentProperties.put("adk.messaging.mode", "Push");
        agentProperties.put("adk.messaging.transport", "http");
        agentProperties.put("adk.messaging.pullFrequency", "30000");
        agentProperties.put("adk.messaging.maxBufferSize", "32000");

        Properties httpProperties = new Properties();
        httpProperties.put("port", "25101");

        Properties httpsProperties = new Properties();

        return new EventReporterAgent("test.publisher.agent", zoneConfig, agentProperties,
                httpProperties, httpsProperties, "TestZone",
                "http://10.163.6.73:50002/TestZone", SIFVersion.SIF23);
    }

    @Test
    public void shouldExtendAgent() {
        ZoneConfigurator mockZoneConfigurator = Mockito.mock(ZoneConfigurator.class);
        EventReporterAgent agent = createEventReporterAgent(mockZoneConfigurator);
        Assert.assertTrue("EventReporterAgent should extend agent", agent instanceof Agent);
    }

    @Test
    public void shouldCreateAndConfigureAgent() throws Exception {

        ZoneConfigurator mockZoneConfigurator = Mockito.mock(ZoneConfigurator.class);
        EventReporterAgent agent = createEventReporterAgent(mockZoneConfigurator);

        agent.startAgent();

        //confirm properties are as expected
        AgentProperties props = agent.getProperties();
        Assert.assertEquals("Push", props.getProperty("adk.messaging.mode"));
        Assert.assertEquals("http", props.getProperty("adk.messaging.transport"));
        Assert.assertEquals("30000", props.getProperty("adk.messaging.pullFrequency"));
        Assert.assertEquals("32000", props.getProperty("adk.messaging.maxBufferSize"));

        Assert.assertEquals("test.publisher.agent", agent.getId());

        TransportManager transportManager = agent.getTransportManager();
        Transport transport = transportManager.getTransport("http");
        TransportProperties transportProperties = transport.getProperties();
        Assert.assertEquals("25101", transportProperties.getProperty("port"));

        Zone[] allZones = agent.getZoneFactory().getAllZones();
        Assert.assertNotNull("Agents zones should not be null", allZones);
        Assert.assertEquals("Agent should be configured with one zone", 1, allZones.length);
        Assert.assertNotNull("Agent's zone should not be null", allZones[0]);
        Assert.assertEquals("Agent's zone Id should be TestZone",
                "TestZone", allZones[0].getZoneId());
        Assert.assertEquals("Agent's zone URL should be http://10.163.6.73:50002/TestZone",
                "http://10.163.6.73:50002/TestZone", allZones[0].getZoneUrl().toString());

        Mockito.verify(mockZoneConfigurator,
                Mockito.times(1)).configure(Mockito.any(Zone[].class));
    }
}
