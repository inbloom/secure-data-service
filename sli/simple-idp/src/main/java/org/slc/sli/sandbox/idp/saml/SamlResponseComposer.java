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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.slc.sli.common.encrypt.security.saml2.XmlSignatureHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Handles creating and signing SAMLResponse xmls
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class SamlResponseComposer {
    
    @Autowired
    XmlSignatureHelper signer;
    
    private static final String ATTRIBUTE_NAME_BEGIN_TEMPLATE = "<saml:Attribute Name=\"__NAME__\">";
    private static final String ATTRIBUTE_VALUE_TEMPLATE = "<saml:AttributeValue xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='xs:string'>__VALUE__</saml:AttributeValue>";
    private static final String ATTRIBUTE_NAME_END_TEMPLATE = "</saml:Attribute>";
    
    public String componseResponse(String destination, String issuer, String requestId, String userId,
            Map<String, String> attributes, List<String> roles) {
        
        String unsignedResponse = createUnsignedResponse(destination, issuer, requestId, userId, attributes, roles);
        byte[] signedResponse = signResponse(unsignedResponse);
        return Base64.encodeBase64String(signedResponse);
    }
    
    private String createUnsignedResponse(String destination, String issuer, String requestId, String userId,
            Map<String, String> attributes, List<String> roles) {
        String template;
        try {
            template = IOUtils.toString(this.getClass().getResourceAsStream("/samlResponseTemplate.xml"));
        } catch (IOException e) {
            throw new SamlProcessException(e);
        }
        
        template = template.replace("__RESPONSE_ID__", UUID.randomUUID().toString());
        template = template.replace("__ASSERTION_ID__", UUID.randomUUID().toString());
        template = template.replace("__REQUEST_ID__", requestId);
        template = template.replace("__ISSUE_INSTANT__", 
                new DateTime().withZone(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis()));
        template = template.replace("__DESTINATION__", destination);
        template = template.replace("__ISSUER__", issuer);
        template = template.replace("__TRANSIENT_ID__", UUID.randomUUID().toString()); //must be at least a 128-bit random string
        DateTime notOnOrAfter = new DateTime();
        notOnOrAfter = notOnOrAfter.plusMinutes(10);
        template = template.replace("__NOT_ON_OR_AFTER__", 
                notOnOrAfter.withZone(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis()));
        
        StringBuilder buf = new StringBuilder();
        addAttribute(buf, "userId", userId);
        if (attributes != null) {
            for (Map.Entry<String, String> attr : attributes.entrySet()) {
                if (attr.getKey() != null && attr.getValue() != null) {
                    addAttribute(buf, attr.getKey(), attr.getValue());
                }
            }
        }
        
        if (roles != null && !roles.isEmpty()) {
            buf.append(ATTRIBUTE_NAME_BEGIN_TEMPLATE.replace("__NAME__", "roles"));
            for (String role : roles) {
                buf.append(ATTRIBUTE_VALUE_TEMPLATE.replace("__VALUE__", role));
            }
            buf.append(ATTRIBUTE_NAME_END_TEMPLATE);
        }
        
        template = template.replace("__ATTRIBUTES__", buf.toString());
        return template;
    }
    
    private void addAttribute(StringBuilder buf, String key, String value) {
        buf.append(ATTRIBUTE_NAME_BEGIN_TEMPLATE.replace("__NAME__", key));
        buf.append(ATTRIBUTE_VALUE_TEMPLATE.replace("__VALUE__", value));
        buf.append(ATTRIBUTE_NAME_END_TEMPLATE);
    }
    
    private byte[] signResponse(String template) {
        try {
            InputSource stringSource = new InputSource();
            stringSource.setCharacterStream(new StringReader(template));
            
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            Document unsignedDoc = docFactory.newDocumentBuilder().parse(stringSource);
            Document signedDoc = signer.signSamlAssertion(unsignedDoc);
            
            // any transforms (indentation, etc) will break the XML Signatures. No touch!
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            trans.transform(new DOMSource(signedDoc), new StreamResult(bos));
            return bos.toByteArray();
            
        } catch (SAXException e) {
            // holy checked exception list, Batman!
            throw new SamlProcessException(e);
        } catch (IOException e) {
            throw new SamlProcessException(e);
        } catch (ParserConfigurationException e) {
            throw new SamlProcessException(e);
        } catch (GeneralSecurityException e) {
            throw new SamlProcessException(e);
        } catch (TransformerException e) {
            throw new SamlProcessException(e);
        } catch (MarshalException e) {
            throw new SamlProcessException(e);
        } catch (XMLSignatureException e) {
            throw new SamlProcessException(e);
        }
    }
    
}
