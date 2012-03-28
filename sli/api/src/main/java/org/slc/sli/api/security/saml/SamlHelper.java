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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.slc.sli.api.security.saml2.SAML2Validator;

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
    private static final String SAML_SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";
    
    private static final Logger LOG = LoggerFactory.getLogger(SamlHelper.class);
    
    public static final Namespace SAML_NS = Namespace.getNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
    public static final Namespace SAMLP_NS = Namespace.getNamespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");
    
    // Jdom converters
    private DOMBuilder builder = new DOMBuilder();
    private DOMOutputter domer = new DOMOutputter();
    
    // W3c stuff
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
        domBuilder = factory.newDocumentBuilder();
        
        transform = TransformerFactory.newInstance().newTransformer();
        transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }
    
    /**
     * Composes AuthnRequest using post binding
     * 
     * @param destination
     * @return
     */
    public Pair<String, String> createSamlAuthnRequestForRedirect(String destination) {
        return composeAuthnRequest(destination, POST_BINDING);
    }
    
    /**
     * Composes AuthnRequest using artifact binding
     * 
     * @param destination
     * @return
     */
    public Pair<String, String> createSamlAuthnRequestForRedirectArtifact(String destination) {
        return composeAuthnRequest(destination, ARTIFACT_BINDING);
    }
    
    /**
     * Composes LogoutRequest (binding-agnostic).
     * 
     * @param destination
     * @return
     */
    public String createSamlLogoutRequest(String destination) {
        return composeLogoutRequest(destination);
    }
    
    public String createSamlLogoutResponse(String destination) {
        return composeLogoutResponse(destination);
    }
    
    /**
     * Converts post data containing saml xml data to a jdom document
     * 
     * @param postData
     * @return
     * @throws Exception
     */
    public Document decodeSamlPost(String postData) {
        String base64Decoded = decode(postData);
        
        try {
            
            org.w3c.dom.Document doc = domBuilder.parse(new InputSource(new StringReader(base64Decoded)));
            
            // TODO verify digest and signature --> update to validator.isDocumentValidAndTrusted()
            if (!validator.isDocumentValid(doc)) {
                throw new IllegalArgumentException("Invalid SAML message");
            }
            
            return this.builder.build(doc);
            
        } catch (Exception e) {
            LOG.error("Error unmarshalling saml post", e);
            throw new IllegalArgumentException("Posted SAML isn't valid");
        }
    }
    
    /**
     * Generates AuthnRequest and converts it to valid form for HTTP-Redirect binding
     * 
     * AssertionConsumerServiceURL attribute can be added to root element, but seems not required. If added, must match an
     * endpoint that was sent to the idp during federation (sp.xml)
     * SPNameQualifier attribute can be added to NameId, but seems not required. Same as IssuerName
     * 
     * @param destination idp url to where the message is going
     * @return {generated messageId, deflated, base64-encoded and url encoded saml message} java doesn't have tuples :(
     * @throws Exception
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    private Pair<String, String> composeAuthnRequest(String destination, String binding) {
        Document doc = new Document();
        
        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("AuthnRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("IssueInstant", new DateTime(DateTimeZone.UTC).toString()));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        doc.getRootElement().getAttributes().add(new Attribute("ForceAuthn", "false"));
        doc.getRootElement().getAttributes().add(new Attribute("IsPassive", "false"));
        doc.getRootElement().getAttributes().add(new Attribute("ProtocolBinding", binding));
        
        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(this.issuerName);
        
        doc.getRootElement().addContent(issuer);
        
        Element nameId = new Element("NameIDPolicy", SAMLP_NS);
        nameId.getAttributes().add(new Attribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        nameId.getAttributes().add(new Attribute("AllowCreate", "true"));
        nameId.getAttributes().add(new Attribute("SPNameQualifier", this.issuerName));
        
        doc.getRootElement().addContent(nameId);
        
        Element authnContext = new Element("RequestedAuthnContext", SAMLP_NS);
        authnContext.getAttributes().add(new Attribute("Comparison", "exact"));
        Element classRef = new Element("AuthnContextClassRef", SAML_NS);
        classRef.addContent("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        authnContext.addContent(classRef);
        
        doc.getRootElement().addContent(authnContext);
        
        // add signature and digest here
        
        try {
            String xmlString = nodeToXmlString(domer.output(doc));
            
            LOG.debug(xmlString);
            
            return Pair.of(id, xmlToEncodedString(xmlString));
        } catch (Exception e) {
            LOG.error("Error composing AuthnRequest", e);
            throw new IllegalArgumentException("Couldn't compose AuthnRequest", e);
        }
    }
    
    /**
     * Composes a Logout Request (for SP-initiated Single Logout). Example of Logout Request:
     * 
     * <samlp:LogoutRequest 
     *   xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
     *   ID="21B78E9C6C8ECF16F01E4A0F15AB2D46"
     *   IssueInstant="2010-04-28T21:36:11.230Z"
     *   Version="2.0">
     *   <saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">https://dloomac.service-now.com</saml2:Issuer>
     *   <saml:NameID xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
     *     Format="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"
     *     NameQualifier="http://idp.ssocircle.com" 
     *     SPNameQualifier="https://dloomac.service-now.com/navpage.do">
     *       david.loo@service-now.com
     *     </saml:NameID>
     *   <samlp:SessionIndex>s211b2f811485b2a1d2cc4db2b271933c286771104</samlp:SessionIndex>
     * </samlp:LogoutRequest>
     * 
     * @param destination IDP endpoint
     * @return deflated, base64-encoded and url encoded saml message
     */
    @SuppressWarnings("unchecked")
    private String composeLogoutRequest(String destination) {
        Document doc = new Document();
        
        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("LogoutRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("IssueInstant", new DateTime(DateTimeZone.UTC).toString()));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        
        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(this.issuerName);
        doc.getRootElement().addContent(issuer);
        
        Element nameId = new Element("NameID", SAML_NS);
        nameId.getAttributes().add(new Attribute("Format", "http://schemas.xmlsoap.org/claims/UPN")); // http://schemas.xmlsoap.org/ws/2005/05/identity/claims/upn
        nameId.getAttributes().add(new Attribute("NameQualifier", destination)); 
        nameId.getAttributes().add(new Attribute("SPNameQualifier", this.issuerName));
        // need to add content for username
        
        doc.getRootElement().addContent(nameId);
        
        // SAML 2.0 specification defines SessionIndex tag as:
        // The identifier that indexes this session at the message recipient
        // (http://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf)
        // SessionIndex is optional --> can't find any reference where we store session index from IDP
        // If we ever choose to store sessionIndex with user, it should be added to LogoutRequest here
        
        // add signature and digest here
        
        try {
            String xmlString = nodeToXmlString(domer.output(doc));
            LOG.debug(xmlString);
            return xmlToEncodedString(xmlString);
        } catch (Exception e) {
            LOG.error("Error composing LogoutRequest", e);
            throw new IllegalArgumentException("Couldn't compose LogoutRequest", e);
        }
    }
    
    /**
     * Composes a Logout Response (for IDP-initiated Single Logout).
     * 
     * @param destination IDP endpoint
     * @return deflated, base64-encoded and url encoded saml message
     */
    @SuppressWarnings("unchecked")
    private String composeLogoutResponse(String destination) {
        Document doc = new Document();
        
        String id = "sli-" + UUID.randomUUID().toString();
        doc.setRootElement(new Element("LogoutResponse", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("IssueInstant", new DateTime(DateTimeZone.UTC).toString()));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        
        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(this.issuerName);
        doc.getRootElement().addContent(issuer);
        
        Element status = new Element("Status", SAMLP_NS);
        Element statusCode = new Element("StatusCode", SAMLP_NS);
        statusCode.getAttributes().add(new Attribute("Value", SAML_SUCCESS));
        status.addContent(statusCode);
        doc.getRootElement().addContent(status);
        
        try {
            String xmlString = nodeToXmlString(domer.output(doc));
            LOG.debug(xmlString);
            return xmlToEncodedString(xmlString);
        } catch (Exception e) {
            LOG.error("Error composing LogoutResponse", e);
            throw new IllegalArgumentException("Couldn't compose LogoutResponse", e);
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
        
        LOG.debug("Decrypted SAML: \n{}\n", base64Decoded);
        return base64Decoded;
    }
    
    /**
     * Converts w3c node to string representation
     * 
     * @param node to convert
     * @return string respresentation of the node
     * @throws TransformerException
     */
    private String nodeToXmlString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();
        transform.transform(new DOMSource(node), new StreamResult(sw));
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
