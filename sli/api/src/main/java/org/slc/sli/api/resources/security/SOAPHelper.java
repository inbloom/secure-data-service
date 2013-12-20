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
import org.opensaml.ws.soap.client.http.TLSProtocolSocketFactory;
import org.opensaml.ws.soap.common.SOAPException;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.BasicParserPool;
import org.slc.sli.api.exceptions.APIAccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * @author: npandey
 */
@Component
public class SOAPHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SOAPHelper.class);

    /**
     * Method to open a SOAP channel.
     * @param soapEnvelope
     * @param destinationUrl
     * @param pkEntry
     * @return
     */
    protected XMLObject sendSOAPCommunication(Envelope soapEnvelope, String destinationUrl, KeyStore.PrivateKeyEntry pkEntry) {
        BasicSOAPMessageContext soapContext = new BasicSOAPMessageContext();
        soapContext.setOutboundMessage(soapEnvelope);

        // Build the SOAP client
        HttpClientBuilder clientBuilder = new HttpClientBuilder();

        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();
        ClientCertKeyManager keyManager = new ClientCertKeyManager(pkEntry.getPrivateKey(), x509Certificate);
        clientBuilder.setHttpsProtocolSocketFactory(new TLSProtocolSocketFactory(keyManager, null));

        HttpSOAPClient soapClient = new HttpSOAPClient(clientBuilder.buildClient(), new BasicParserPool());

        try {
            soapClient.send(destinationUrl, soapContext);
        } catch (SOAPException ex) {
            LOG.error("SOAP communication failed", ex);
            handleSOAPCommunicationExceptions(destinationUrl);
        } catch (org.opensaml.xml.security.SecurityException ex) {
            LOG.error("SOAP communication failed", ex);
            handleSOAPCommunicationExceptions(destinationUrl);
        }

        return soapContext.getInboundMessage();
    }

    private void handleSOAPCommunicationExceptions(String url) {
        throw new APIAccessDeniedException("Access Denied: SOAP communication to " + url + " failed");
    }
}
