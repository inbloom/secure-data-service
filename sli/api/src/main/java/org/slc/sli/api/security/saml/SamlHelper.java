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


package org.slc.sli.api.security.saml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.slc.sli.common.encrypt.security.saml2.SAML2Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Handles Saml composing, parsing and validating (signatures)
 *
 * @author dkornishev
 *
 */
@Component
public class SamlHelper {

    private static final String POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private static final String SUCCESS_STATUS = "urn:oasis:names:tc:SAML:2.0:status:Success";

    public static final Namespace SAML_NS = Namespace.getNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
    public static final Namespace SAMLP_NS = Namespace.getNamespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");

    // Jdom converters - not thread safe?
    private DOMBuilder builder = new DOMBuilder();
    private DOMOutputter domer = new DOMOutputter();

    // W3c stuff - not thread safe
    private DocumentBuilder domBuilder;
    private Transformer transform;

    @Value("${sli.security.sp.issuerName}")
    private String issuerName;

    @Autowired
    private SAML2Validator validator;

    @PostConstruct
    public void init() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        domBuilder = factory.newDocumentBuilder();

        transform = TransformerFactory.newInstance().newTransformer();
        transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    /**
     * Composes AuthnRequest using post binding
     *
     * @param destination
     *            idp endpoint
     * @param forceAuthn
     *            boolean indicating whether authentication at the idp should be enforced
     * @param idpType
     *            identifier for the type of IDP (1=SimpleIDP, 2=OpenAM, 3=ADFS, Sitminder=4)
     * @return pair {saml message id, encoded saml message}
     */
    public Pair<String, String> createSamlAuthnRequestForRedirect(String destination, boolean forceAuthn, int idpType) {
        return composeAuthnRequest(destination, POST_BINDING, forceAuthn, idpType);
    }

    /**
     * Converts post data containing saml xml data to a jdom document
     *
     * @param postData String representing base 64 encoded SAML assertion.
     * @return jdom document representation of the SAML assertion POSTed.
     * @throws Exception
     */
    public Document decodeSamlPost(String postData) {

        if (postData == null || postData.isEmpty()) {
            throw new IllegalArgumentException("Empty SAML message");
        }

        String base64Decoded = decode(postData);
        try {
            org.w3c.dom.Document doc = null;
            synchronized (domBuilder) {
                doc = domBuilder.parse(new InputSource(new StringReader(base64Decoded)));
            }

            Document jdomDocument = null;
            synchronized (builder) {
                jdomDocument = builder.build(doc);
            }

            Element status = jdomDocument.getRootElement().getChild("Status", SAMLP_NS);
            Element statusCode = status.getChild("StatusCode", SAMLP_NS);
            String statusValue = statusCode.getAttributeValue("Value");
            if (!statusValue.equals(SUCCESS_STATUS)) {
                error("SAML Response did not have a success status, instead status was {}", statusValue);
            }

            synchronized (validator) {
                String issuer = jdomDocument.getRootElement().getChildText("Issuer", SAML_NS);
                if (!validator.isDocumentTrustedAndValid(doc, issuer)) {
                    throw new IllegalArgumentException("Invalid SAML message");
                }
            }
            return jdomDocument;
        } catch (Exception e) {
            error("Error unmarshalling saml post", e);
            throw (RuntimeException) new IllegalArgumentException("Posted SAML isn't valid").initCause(e);
        }
    }


    /**
     * Generates AuthnRequest and converts it to valid form for HTTP-Redirect binding
     *
     * AssertionConsumerServiceURL attribute can be added to root element, but seems not required.
     * If added, must match an
     * endpoint that was sent to the idp during federation (sp.xml)
     * SPNameQualifier attribute can be added to NameId, but seems not required. Same as IssuerName
     *
     * @param destination
     *            idp url to where the message is going
     * @param binding
     *            binding to be specified in saml
     * @param forceAuthn
     *            boolean indicating whether authentication at the idp should be forced onto user
     * @param idpType
     *            identifier for the type of IDP (1=SimpleIDP, 2=OpenAM, 3=ADFS, Sitminder=4)
     * @return {generated messageId, deflated, base64-encoded and url encoded saml message} java
     *         doesn't have tuples :(
     * @throws Exception
     *
     */
    @SuppressWarnings("unchecked")
    private Pair<String, String> composeAuthnRequest(String destination, String binding, boolean forceAuthn, int idpType) {
        Document doc = new Document();

        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("AuthnRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes()
        .add(new Attribute("IssueInstant",
                new DateTime(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis())));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        doc.getRootElement().getAttributes().add(new Attribute("ForceAuthn", String.valueOf(forceAuthn)));
        doc.getRootElement().getAttributes().add(new Attribute("IsPassive", "false"));
        doc.getRootElement().getAttributes().add(new Attribute("ProtocolBinding", binding));

        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(issuerName);

        doc.getRootElement().addContent(issuer);

        Element nameId = new Element("NameIDPolicy", SAMLP_NS);
        nameId.getAttributes().add(new Attribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        nameId.getAttributes().add(new Attribute("AllowCreate", "true"));
        nameId.getAttributes().add(new Attribute("SPNameQualifier", issuerName));

        doc.getRootElement().addContent(nameId);

        if (idpType != 4) {
            Element authnContext = new Element("RequestedAuthnContext", SAMLP_NS);
            authnContext.getAttributes().add(new Attribute("Comparison", "exact"));
            Element classRef = new Element("AuthnContextClassRef", SAML_NS);
            classRef.addContent("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
            authnContext.addContent(classRef);

            doc.getRootElement().addContent(authnContext);
        }

        // add signature and digest here
        try {
            org.w3c.dom.Document w3Doc = null;
            synchronized (domer) {
                w3Doc = domer.output(doc);
            }
            String xmlString = nodeToXmlString(w3Doc);
            debug(xmlString);
            return Pair.of(id, xmlToEncodedString(xmlString));
        } catch (Exception e) {
            error("Error composing AuthnRequest", e);
            throw new IllegalArgumentException("Couldn't compose AuthnRequest", e);
        }
    }
    /**
     * Decodes post body in accordance to SAML 2 spec
     *
     * @param postData
     * @return decoded string
     */
    private String decode(String postData) {
        String trimmed = postData.replaceAll("\r\n", "");
        String base64Decoded = new String(Base64.decodeBase64(trimmed));

        debug("Decrypted SAML: \n{}\n", base64Decoded);
        return base64Decoded;
    }

    /**
     * Converts w3c node to string representation
     *
     * @param node
     *            to convert
     * @return string representation of the node
     * @throws TransformerException
     */
    private String nodeToXmlString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();
        synchronized (transform) {
            transform.transform(new DOMSource(node), new StreamResult(sw));
        }

        return sw.toString();
    }

    /**
     * Converts plain-text xml to SAML-spec compliant string for HTTP-Redirect binding
     *
     * @param xml
     * @return
     * @throws IOException
     */
    private String xmlToEncodedString(String xml) throws IOException {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
        deflaterOutputStream.write(xml.getBytes());
        deflaterOutputStream.close();
        String base64 = Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
        return URLEncoder.encode(base64, "UTF-8");
    }

}
