/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.slc.sli.common.encrypt.security.saml2.SAML2Validator;
import org.slc.sli.common.encrypt.security.saml2.XmlSignatureHelper;

/**
 * Handles Saml composing, parsing and validating (signatures)
 *
 * @author dkornishev
 *
 */
@Component
public class SamlHelper {

    private static final String POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private static final String ARTIFACT_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact";
    private static final String NAMEID_FORMAT_TRANSIENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";

    // private static final Logger LOG = LoggerFactory.getLogger(SamlHelper.class);

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

    @Autowired
    private XmlSignatureHelper sign;

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
     * @param idpType
     *            identifier for the type of IDP (1=SimpleIDP, 2=OpenAM, 3=ADFS, Sitminder=4)
     * @return
     */
    public Pair<String, String> createSamlAuthnRequestForRedirect(String destination, int idpType) {
        return composeAuthnRequest(destination, POST_BINDING, idpType);
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
     * Composes AuthnRequest using artifact binding
     *
     * @param destination
     * @param idpType
     *            identifier for the type of IDP (1=SimpleIDP, 2=OpenAM, 3=ADFS, Sitminder=4)
     * @return
     */
    public Pair<String, String> createSamlAuthnRequestForRedirectArtifact(String destination, int idpType) {
        return composeAuthnRequest(destination, ARTIFACT_BINDING, idpType);
    }

    /**
     * Composes LogoutRequest (binding-agnostic).
     *
     * @param destination
     *            idp endpoint
     * @param userId
     *            unique identifier of user at idp
     * @param sessionIndex
     *            unique identifier of current user session at idp
     * @return deflated, base64-encoded and url encoded saml message
     */
    public String createSignedSamlLogoutRequest(String destination, String userId, String sessionIndex) {
        return composeSignedLogoutRequest(destination, userId, sessionIndex);
    }

    /**
     * Composes LogoutRequest (binding-agnostic).
     *
     * @param destination
     *            destination idp endpoint
     * @param userId
     *            unique identifier of user at idp
     * @return deflated, base64-encoded and url encoded saml message
     */
    public String createSamlLogoutRequest(String destination, String userId) {
        return composeLogoutRequest(destination, userId);
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

            synchronized (validator) {
                String issuer = jdomDocument.getRootElement().getChildText("Issuer", SAML_NS);
                if (!validator.isDocumentTrustedAndValid(doc, issuer)) {
                    throw new IllegalArgumentException("Invalid SAML message");
                }
            }
            return jdomDocument;
        } catch (Exception e) {
            error("Error unmarshalling saml post", e);
            throw new IllegalArgumentException("Posted SAML isn't valid");
        }
    }

    public Document decodeSamlRedirect(String redirectData) {
        try {
            String xml = encodedStringToXml(redirectData);
            debug(xml);
            org.w3c.dom.Document doc = null;
            synchronized (domBuilder) {
                doc = domBuilder.parse(new InputSource(new StringReader(xml)));
            }

            synchronized (builder) {
                return builder.build(doc);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to decode redirect payload");
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
     * @param idpType
     *            identifier for the type of IDP (1=SimpleIDP, 2=OpenAM, 3=ADFS, Sitminder=4)
     * @return {generated messageId, deflated, base64-encoded and url encoded saml message} java
     *         doesn't have tuples :(
     * @throws Exception
     *
     */
    @SuppressWarnings("unchecked")
    private Pair<String, String> composeAuthnRequest(String destination, String binding, int idpType) {
        Document doc = new Document();

        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("AuthnRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes()
                .add(new Attribute("IssueInstant",
                        new DateTime(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis())));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        doc.getRootElement().getAttributes().add(new Attribute("ForceAuthn", "true"));
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
     * Composes a Logout Request (for SP-initiated Single Logout). Example of Logout Request:
     *
     * <samlp:LogoutRequest
     * xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
     * ID="21B78E9C6C8ECF16F01E4A0F15AB2D46"
     * IssueInstant="2010-04-28T21:36:11.230Z"
     * Version="2.0">
     * <saml:Issuer
     * xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">https://dloomac.service-now.com
     * </saml2:Issuer>
     * <saml:NameID xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
     * Format="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"
     * NameQualifier="http://idp.ssocircle.com"
     * SPNameQualifier="https://dloomac.service-now.com/navpage.do">
     * david.loo@service-now.com
     * </saml:NameID>
     * <samlp:SessionIndex>s211b2f811485b2a1d2cc4db2b271933c286771104</samlp:SessionIndex>
     * </samlp:LogoutRequest>
     *
     * @param destination
     *            IDP endpoint
     * @return deflated, base64-encoded and url encoded saml message
     */
    @SuppressWarnings("unchecked")
    private String composeLogoutRequest(String destination, String userId) {
        Document doc = new Document();

        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("LogoutRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes()
                .add(new Attribute("IssueInstant",
                        new DateTime(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis())));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));

        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(issuerName);
        doc.getRootElement().addContent(issuer);

        Element nameId = new Element("NameID", SAML_NS);
        nameId.getAttributes().add(new Attribute("Format", NAMEID_FORMAT_TRANSIENT));
        nameId.getAttributes().add(new Attribute("NameQualifier", destination));
        nameId.getAttributes().add(new Attribute("SPNameQualifier", issuerName));
        nameId.setText(userId);

        doc.getRootElement().addContent(nameId);

        // add signature and digest here
        try {
            org.w3c.dom.Document dom = null;
            synchronized (domer) {
                dom = domer.output(doc);
            }

            dom = sign.signSamlAssertion(dom);
            String xmlString = nodeToXmlString(dom);
            debug(xmlString);
            return xmlToEncodedString(xmlString);
        } catch (Exception e) {
            error("Error composing LogoutRequest", e);
            throw new IllegalArgumentException("Couldn't compose LogoutRequest", e);
        }
    }

    /**
     * Composes a Logout Request (for SP-initiated Single Logout). Example of Logout Request:
     *
     * @param destination
     *            IDP endpoint
     * @param userId
     *            unique id of user
     * @param sessionIndex
     *            unique identifier of session at idp
     * @return deflated, base64-encoded and url encoded saml message
     */
    @SuppressWarnings("unchecked")
    private String composeSignedLogoutRequest(String destination, String userId, String sessionIndex) {
        Document doc = new Document();

        if (destination == null) {
            throw new IllegalArgumentException("idp destination cannot be null");
        } else if (userId == null) {
            throw new IllegalArgumentException("user id cannot be null");
        } else if (sessionIndex == null) {
            throw new IllegalArgumentException("session index cannot be null");
        }

        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("LogoutRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes()
                .add(new Attribute("IssueInstant", new DateTime(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTimeNoMillis())));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));

        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(issuerName);
        doc.getRootElement().addContent(issuer);

        Element nameId = new Element("NameID", SAML_NS);
        nameId.getAttributes().add(new Attribute("Format", NAMEID_FORMAT_TRANSIENT));
        nameId.getAttributes().add(new Attribute("NameQualifier", destination));
        nameId.getAttributes().add(new Attribute("SPNameQualifier", issuerName));
        nameId.setText(userId);

        doc.getRootElement().addContent(nameId);

        Element sessionIndexElement = new Element("SessionIndex", SAMLP_NS);
        sessionIndexElement.setText(sessionIndex);
        doc.getRootElement().addContent(sessionIndexElement);

        try {
            org.w3c.dom.Document dom = domer.output(doc);
            dom = sign.signSamlAssertion(dom);
            String xmlString = nodeToXmlString(dom);
            debug(xmlString);
            return xmlToEncodedString(xmlString);
        } catch (Exception e) {
            error("Error composing LogoutRequest", e);
            throw new IllegalArgumentException("Couldn't compose LogoutRequest", e);
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

    private static String encodedStringToXml(String msg) throws DataFormatException, UnsupportedEncodingException {
        // msg = URLDecoder.decode(msg, "UTF-8");

        byte[] bytes = Base64.decodeBase64(msg);

        Inflater inf = new Inflater(true);
        inf.setInput(bytes);

        byte[] result = new byte[10024];

        int len = 0;
        while (!inf.finished()) {
            len += inf.inflate(result);
        }

        inf.end();

        return new String(result, 0, len, "UTF-8");

    }
}
