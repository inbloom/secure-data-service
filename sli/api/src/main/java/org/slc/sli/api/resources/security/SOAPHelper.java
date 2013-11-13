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
package org.slc.sli.api.resources.security;

import org.opensaml.ws.soap.client.BasicSOAPMessageContext;
import org.opensaml.ws.soap.client.http.HttpClientBuilder;
import org.opensaml.ws.soap.client.http.HttpSOAPClient;
import org.opensaml.ws.soap.common.SOAPException;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.BasicParserPool;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * @author: npandey
 */
@Component
public class SOAPHelper {

    /**
     * Method to open a SOAP channel.
     * @param soapEnvelope
     * @param destinationUrl
     * @return
     */
    protected XMLObject sendSOAPCommunication(Envelope soapEnvelope, String destinationUrl) {
        BasicSOAPMessageContext soapContext = new BasicSOAPMessageContext();
        soapContext.setOutboundMessage(soapEnvelope);

        // Build the SOAP client
        HttpClientBuilder clientBuilder = new HttpClientBuilder();
        HttpSOAPClient soapClient = new HttpSOAPClient(clientBuilder.buildClient(), new BasicParserPool());

        try {
            soapClient.send(destinationUrl, soapContext);
        } catch (SOAPException ex) {
            error("SOAP communication failed", ex);
            handleSOAPCommunicationExceptions(destinationUrl);
        } catch (org.opensaml.xml.security.SecurityException ex) {
            error("SOAP communication failed", ex);
            handleSOAPCommunicationExceptions(destinationUrl);
        }

        return soapContext.getInboundMessage();
    }

    private void handleSOAPCommunicationExceptions(String url) {
        throw new APIAccessDeniedException("Access Denied: SOAP communication to " + url + " failed");
    }
}
