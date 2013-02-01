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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import openadk.library.ADK;
import openadk.library.SIFElement;
import openadk.library.SIFParser;
import openadk.library.SIFWriter;
import openadk.library.impl.SIF_Message;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_Register;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MockZis, responsible for parsing incoming SIF messages and broadcasting messages.
 * @author jtully
 *
 */
@Component
public class MockZis {

    private static final Logger LOG = LoggerFactory.getLogger(MockZis.class);

    private Set<String> agentCallbackUrls = new HashSet<String>();

    /**
     * Parse and process a SIF message.
     */
    public void parseSIFMessage(String sifString) {
        try {
            SIFParser parser = SIFParser.newInstance();
            SIFElement sifElem = parser.parse(sifString);

            if (sifElem instanceof SIF_Register) {
                processRegisterMessage((SIF_Register) sifElem);
            }
        } catch (Exception e) {
            LOG.error("Exception parsing SIF message: ", e);
        }
    }

    private String sifElementToString(SIFElement sifElem) {
        StringWriter sw = new StringWriter();
        SIFWriter writer = new SIFWriter(sw);
        writer.suppressNamespace(true);
        writer.write(sifElem);
        writer.flush();
        writer.close();
        return sw.toString();
    }

    /**
     * Broadcast an xml message to all registered agents.
     */
    public void broadcastMessage(String xmlMessage) {
        for (String callbackUrl : agentCallbackUrls) {
            LOG.info("Broadcasting message to " + callbackUrl);
            try {
                postMessage(new URL(callbackUrl), xmlMessage);
            } catch (MalformedURLException e) {
                LOG.error("Agent callback URL error: ", e);
            }
        }

    }

    private void postMessage(URL url, String xmlMessage) {
        DataOutputStream outStream = null;
        DataInputStream inStream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml;charset=utf-8");
            connection.setRequestProperty("Content-Length", "" + xmlMessage.length());

            connection.setDoOutput(true);
            connection.setDoInput(true);

            outStream = new DataOutputStream(connection.getOutputStream());

            // Send request
            outStream.writeBytes(xmlMessage);
            outStream.flush();

            // Get Response
            inStream = new DataInputStream(connection.getInputStream());
            StringWriter writer = new StringWriter();
            IOUtils.copy(inStream, writer, "UTF-8");
            String response = writer.toString();
            LOG.info("Response to agent POST: " + response);

        } catch (Exception e) {
            LOG.error("Error POSTing message: ", e);
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                LOG.error("Error closing streams: ", e);
            }
        }
    }

    @PostConstruct
    public void setup() throws Exception {
        ADK.initialize();
    }

    public String createAckString() {
        SIF_Message message = new SIF_Message();
        SIF_Ack ack = message.ackStatus(0);
        return sifElementToString(ack);
    }

    private void processRegisterMessage(SIF_Register sifReg) {
        String agentCallbackUrl = sifReg.getSIF_Protocol().getSIF_URL();
        LOG.info("Registered agent with callback " + agentCallbackUrl);
        agentCallbackUrls.add(agentCallbackUrl);
    }

    public void getAgentUrls(Set<String> agentCallbackUrls) {
        this.agentCallbackUrls = agentCallbackUrls;
    }

    public Set<String> getAgentUrls() {
        return agentCallbackUrls;
    }

}
