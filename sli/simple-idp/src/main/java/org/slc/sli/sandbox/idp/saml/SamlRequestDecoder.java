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


package org.slc.sli.sandbox.idp.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Decodes SAMLRequests and parses out the relevant information.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Component
public class SamlRequestDecoder {

    // private static final Logger LOG = LoggerFactory.getLogger(SamlRequestDecoder.class);

    @Value("${sli.simple-idp.cot}")
    private String cotString;

    private Map<String, String> cot;

    @SuppressWarnings("unused")
    @PostConstruct
    void initialize() {
        cot = new HashMap<String, String>();
        String[] trustedIssuers = cotString.split(",");
        for (String trustedIssuerPair : trustedIssuers) {
            String[] trustedIssuer = trustedIssuerPair.split("=");
            cot.put(trustedIssuer[0], trustedIssuer[1]);
        }
    }

    /**
     * Holds saml request info
     */
    public static class SamlRequest {
        private final String spDestination;
        private final String id;
        private final String idpDestination;
        private final boolean forceAuthn;

        SamlRequest(String spDestination, String idpDestination, String id, boolean forceAuthn) {
            this.spDestination = spDestination;
            this.idpDestination = idpDestination;
            this.id = id;
            this.forceAuthn = forceAuthn;
        }

        public String getIdpDestination() {
            return idpDestination;
        }

        public String getSpDestination() {
            return spDestination;
        }

        public String getId() {
            return id;
        }

        public boolean isForceAuthn() {
            return forceAuthn;
        }
    }

    public SamlRequest decode(String encodedSamlRequest) {
        byte[] decodedCompressed = Base64.decodeBase64(encodedSamlRequest);
        Inflater inflater = new Inflater(true);
        InflaterInputStream xmlInputStream = new InflaterInputStream(new ByteArrayInputStream(decodedCompressed),
                inflater);
        Document doc;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            
            doc = docBuilder.parse(xmlInputStream);
        } catch (ParserConfigurationException e) {
            throw new SamlProcessException(e);
        } catch (SAXException e) {
            throw new SamlProcessException(e);
        } catch (IOException e) {
            throw new SamlProcessException(e);
        }
        Element element = doc.getDocumentElement();
        String id = element.getAttribute("ID");
        boolean forceAuthn = Boolean.valueOf(element.getAttribute("ForceAuthn"));
        String simpleIDPDestination = element.getAttribute("Destination");
        NodeList nodes = element.getElementsByTagName("saml:Issuer");
        String issuer = null;
        if (nodes.getLength() > 0) {
            Node item = nodes.item(0);
            issuer = item.getFirstChild().getNodeValue();
        } else {
            throw new IllegalArgumentException("No Issuer element on AuthnRequest");
        }

        if (id == null) {
            throw new IllegalArgumentException("No ID attribute on AuthnRequest.");
        }
        String responseDestination = cot.get(issuer);
        if (responseDestination == null) {
            throw new IllegalArgumentException("Issuer of AuthnRequest is unknown.");
        }

        return new SamlRequest(responseDestination, simpleIDPDestination, id, forceAuthn);
    }

    public void setCotString(String cotString) {
        this.cotString = cotString;
    }

}
