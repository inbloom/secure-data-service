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
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
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
import org.jdom.output.DOMOutputter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.opensaml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.opensaml.saml2.binding.artifact.SAML2ArtifactType0004;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.ChainingEncryptedKeyResolver;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.encryption.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.validation.ValidationException;
import org.slc.sli.common.encrypt.security.saml2.SAML2Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.domain.Entity;

/**
 * Handles Saml composing, parsing and validating (signatures)
 *
 * @author dkornishev
 *
 */
@Component
public class SamlHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SamlHelper.class);

    private static final String POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private static final String SUCCESS_STATUS = "urn:oasis:names:tc:SAML:2.0:status:Success";

    public static final Namespace SAML_NS = Namespace.getNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
    public static final Namespace SAMLP_NS = Namespace.getNamespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");

    private DOMOutputter domer = new DOMOutputter();

    // W3c stuff - not thread safe
    private DocumentBuilder domBuilder;
    private Transformer transform;

    @Value("${sli.security.sp.issuerName}")
    private String issuerName;

    @Autowired
    private SAML2Validator validator;

    @Autowired
    private RealmHelper realmHelper;

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
     * Converts post data containing saml xml data to a document
     *
     * @param data String representing base 64 encoded saml data.
     * @return document representation of the saml data.
     * @throws Exception
     */
    public org.w3c.dom.Document parseToDoc(String data) {
        org.w3c.dom.Document doc = null;
        try {
            doc = domBuilder.parse(new InputSource(new StringReader(data)));
        } catch (Exception e) {
            LOG.error("Error parsing saml post", e);
            throw (RuntimeException) new IllegalArgumentException("Posted SAML isn't valid").initCause(e);
        }
        return doc;
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
            LOG.debug(xmlString);
            return Pair.of(id, xmlToEncodedString(xmlString));
        } catch (Exception e) {
            LOG.error("Error composing AuthnRequest", e);
            throw new IllegalArgumentException("Couldn't compose AuthnRequest", e);
        }
    }
    /**
     * Decodes post body in accordance to SAML 2 spec
     *
     * @param postData
     * @return decoded string
     */
    public String decode(String postData) {
        String trimmed = postData.replaceAll("\r\n", "");
        String base64Decoded = new String(Base64.decodeBase64(trimmed));

        LOG.debug("Decrypted SAML: \n{}\n", base64Decoded);
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

    public Signature getDigitalSignature(KeyStore.PrivateKeyEntry keystoreEntry) {
        Signature signature = (Signature) Configuration.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME)
                .buildObject(Signature.DEFAULT_ELEMENT_NAME);

        Credential signingCredential = initializeCredentialsFromKeystore(keystoreEntry);
        signature.setSigningCredential(signingCredential);

        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);

        SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
        try {
            SecurityHelper.prepareSignatureParams(signature, signingCredential, secConfig, null);
        } catch (org.opensaml.xml.security.SecurityException  ex) {
            LOG.error("Error composing artifact resolution request: Failed to generate digital signature");
            throw new IllegalArgumentException("Couldn't compose artifact resolution request", ex);
        }

        return signature;
    }

    private Credential initializeCredentialsFromKeystore(KeyStore.PrivateKeyEntry keystoreEntry) {
        BasicX509Credential signingCredential = new BasicX509Credential();

        PrivateKey pk = keystoreEntry.getPrivateKey();
        X509Certificate certificate = (X509Certificate) keystoreEntry.getCertificate();

        signingCredential.setEntityCertificate(certificate);
        signingCredential.setPrivateKey(pk);

        return signingCredential;
    }

    /**
     * Validates that the certificate in the saml assertion is valid and trusted.
     * @param samlResponse
     *      SAML response form the IdP.
     */
    public void validateCertificate(Assertion assertion, String issuer)  {
        validateSignatureFormat(assertion.getSignature());

        try {
            if (!validator.isDocumentTrusted(assertion.getDOM(), issuer)) {
                throw new APIAccessDeniedException("Invalid SAML message: Certificate is not trusted");
            }
        } catch (Exception e) {
            handleSignatureValidationErrors();
        }
    }

    private void validateSignatureFormat(Signature signature) {
        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();

        try {
            profileValidator.validate(signature);
        } catch (ValidationException e) {
            handleSignatureValidationErrors();
        }
    }

    private void handleSignatureValidationErrors() {
        throw new IllegalArgumentException("Invalid SAML message: couldn't verify signature");
    }
    /**
     *
     * @param samlAssertion
     * @return
     */
    public LinkedMultiValueMap<String, String> extractAttributesFromResponse(Assertion samlAssertion) {
        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();

        AttributeStatement attributeStatement = samlAssertion.getAttributeStatements().get(0);

        for (org.opensaml.saml2.core.Attribute attribute : attributeStatement.getAttributes()) {
            String samlAttributeName = attribute.getName();
            List<XMLObject> valueObjects = attribute.getAttributeValues();
            for (XMLObject valueXmlObject : valueObjects) {
                attributes.add(samlAttributeName, valueXmlObject.getDOM().getTextContent());
            }
        }
        return attributes;
    }

    public String getArtifactUrl(String realmId, String artifact) {
        byte[] sourceId =  retrieveSourceId(artifact);
        Entity realm = realmHelper.findRealmById(realmId);

        if (realm == null) {
            LOG.error("Invalid realm: " + realmId);
            throw new APIAccessDeniedException("Authorization could not be verified.");
        }

        Map<String, Object> idp = (Map<String, Object>) realm.getBody().get("idp");
        String realmSourceId = (String) idp.get("sourceId");
        if (realmSourceId == null || realmSourceId.isEmpty()) {
            LOG.error("SourceId is not configured properly for realm: " + realmId);
            throw new APIAccessDeniedException("Authorization could not be verified.");
        }

        byte[] realmByteSourceId = DatatypeConverter.parseHexBinary(realmSourceId);
        if (!Arrays.equals(realmByteSourceId, sourceId)) {
            LOG.error("SourceId from Artifact does not match configured SourceId for realm: " + realmId);
            throw new APIAccessDeniedException("Authorization could not be verified.");
        }

        return (String) idp.get("artifactResolutionEndpoint");
    }

    private byte[] retrieveSourceId(String artifact) {
        SAML2ArtifactBuilderFactory builder  = new SAML2ArtifactBuilderFactory();
        SAML2ArtifactType0004 art = (SAML2ArtifactType0004) builder.buildArtifact(artifact);
        return art.getSourceID();
    }

    public org.opensaml.saml2.core.Response convertToSAMLResponse(org.w3c.dom.Element element) {
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject responseXmlObj = null;
        try {
            responseXmlObj = unmarshaller.unmarshall(element);
        } catch (UnmarshallingException e) {
            raiseSamlValidationError("Error unmarshalling response from IdP", null);
        }

        return (org.opensaml.saml2.core.Response) responseXmlObj;
    }

    public Assertion getAssertion(org.opensaml.saml2.core.Response samlResponse, KeyStore.PrivateKeyEntry keystoreEntry) {
        Assertion assertion;
        if (isAssertionEncrypted(samlResponse)) {
            assertion = decryptAssertion(samlResponse.getEncryptedAssertions().get(0), keystoreEntry);
        } else {
            assertion = samlResponse.getAssertions().get(0);
        }
        return assertion;
    }

    protected boolean isAssertionEncrypted(org.opensaml.saml2.core.Response samlResponse) {
        if (samlResponse.getEncryptedAssertions() != null && samlResponse.getEncryptedAssertions().size() != 0) {
            return true;
        }
        return false;
    }

    protected Assertion decryptAssertion(EncryptedAssertion encryptedAssertion, KeyStore.PrivateKeyEntry keystoreEntry) {
        BasicX509Credential decryptionCredential = new BasicX509Credential();

        decryptionCredential.setPrivateKey(keystoreEntry.getPrivateKey());

        StaticKeyInfoCredentialResolver resolver = new StaticKeyInfoCredentialResolver(decryptionCredential);

        ChainingEncryptedKeyResolver keyResolver = new ChainingEncryptedKeyResolver();
        keyResolver.getResolverChain().add(new InlineEncryptedKeyResolver());
        keyResolver.getResolverChain().add(new EncryptedElementTypeEncryptedKeyResolver());
        keyResolver.getResolverChain().add(new SimpleRetrievalMethodEncryptedKeyResolver());

        Decrypter decrypter = new Decrypter(null, resolver, keyResolver);
        decrypter.setRootInNewDocument(true);
        Assertion assertion = null;
        try {
            assertion = decrypter.decrypt(encryptedAssertion);
        } catch (DecryptionException e) {
            raiseSamlValidationError("Unable to decrypt SAML assertion", null);
        }
        return assertion;
    }

    public void validateStatus(org.opensaml.saml2.core.Response samlResponse) {
        Status responseStatus = samlResponse.getStatus();
        StatusCode statusCode = responseStatus.getStatusCode();
        String statusValue = statusCode.getValue();

        if (!statusValue.equals(SUCCESS_STATUS)) {
            LOG.error("SAML Response did not have a success status, instead status was {}", statusValue);
        }
    }

    private void raiseSamlValidationError(String message, String targetEdOrg) {
        LOG.error(message);
        throw new APIAccessDeniedException("Authorization could not be verified.", targetEdOrg);
    }
}
