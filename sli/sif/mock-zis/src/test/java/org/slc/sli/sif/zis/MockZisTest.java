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
package org.slc.sli.sif.zis;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import openadk.library.ADKException;
import openadk.library.SIFElement;
import openadk.library.SIFParser;
import openadk.library.infra.SIF_Ack;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * JUnit tests for MockZis
 *
 * @author jtully
 *
 */
public class MockZisTest {

    private MockZis mockZis;

    @Before
    public void setup() throws Exception {
        mockZis = new MockZis();
        mockZis.setup();

    }

    @Test
    public void shouldCreateAckMessages() throws ADKException, IOException {
        String ackString = mockZis.createAckString();

        SIFParser parser = SIFParser.newInstance();
        SIFElement sifElem = parser.parse(ackString);

        Assert.assertTrue("Should create a SIF message",
                (sifElem instanceof SIF_Ack));

    }

    @Test
    public void shouldRegisterAgents() throws IOException {
        Resource xmlFile = new ClassPathResource("TestRegisterMessage.xml");

        StringWriter writer = new StringWriter();
        IOUtils.copy(xmlFile.getInputStream(), writer, "UTF-8");
        String sifString = writer.toString();

        mockZis.parseSIFMessage(sifString);

        //check that the correct URL has been added to the
        Set<String> agentUrls = mockZis.getAgentUrls();

        Assert.assertEquals("Should register one agent", 1, agentUrls.size());
        Assert.assertEquals("Registered agent URL incorrect",
                "http://10.81.1.35:25101/zone/TestZone/", agentUrls.iterator().next());
    }

    //TODO mock the HTTP POST here
    //void broadcastMessage(String xmlMessage)
}
