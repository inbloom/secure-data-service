package org.slc.sli.api.security.saml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Handles Saml composing and parsing
 * 
 * @author dkornishev
 * 
 */
@Component
public class SamlHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(SamlHelper.class);
    
    public static final Namespace SAML_NS = Namespace.getNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
    public static final Namespace SAMLP_NS = Namespace.getNamespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");
    
    private SAXBuilder builder = new SAXBuilder();
    private XMLOutputter formater = new XMLOutputter(Format.getPrettyFormat());
    
    @Value("${sli.security.sp.issuerName}")
    private String issuerName;
    
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
    public Pair<String, String> createSamlAuthnRequestForRedirect(String destination) {
        
        Document doc = new Document();
        
        String id = UUID.randomUUID().toString();
        doc.setRootElement(new Element("AuthnRequest", SAMLP_NS));
        doc.getRootElement().getAttributes().add(new Attribute("ID", id));
        doc.getRootElement().getAttributes().add(new Attribute("Version", "2.0"));
        doc.getRootElement().getAttributes().add(new Attribute("IssueInstant", new DateTime(DateTimeZone.UTC).toString()));
        doc.getRootElement().getAttributes().add(new Attribute("Destination", destination));
        doc.getRootElement().getAttributes().add(new Attribute("ForceAuthn", "false"));
        doc.getRootElement().getAttributes().add(new Attribute("IsPassive", "false"));
        doc.getRootElement().getAttributes().add(new Attribute("ProtocolBinding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        
        Element issuer = new Element("Issuer", SAML_NS);
        issuer.addContent(this.issuerName);
        
        doc.getRootElement().addContent(issuer);
        
        Element nameId = new Element("NameIDPolicy", SAMLP_NS);
        nameId.getAttributes().add(new Attribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"));
        nameId.getAttributes().add(new Attribute("AllowCreate", "true"));
        
        doc.getRootElement().addContent(nameId);
        
        Element authnContext = new Element("RequestedAuthnContext", SAMLP_NS);
        authnContext.getAttributes().add(new Attribute("Comparison", "exact"));
        Element classRef = new Element("AuthnContextClassRef", SAML_NS);
        classRef.addContent("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        authnContext.addContent(classRef);
        
        doc.getRootElement().addContent(authnContext);
        
        String xmlString = formater.outputString(doc);
        LOG.debug(xmlString);
        
        try {
            return Pair.of(id, xmlToEncodedString(xmlString));
        } catch (IOException e) {
            LOG.error("Error generating AuthnRequest", e);
            throw new IllegalStateException("[CRITICAL] Failed to generate AuthnRequest");
        }
    }
    
    /**
     * Converts post data containing saml xml data to a jdom document
     * 
     * @param postData
     * @return
     * @throws Exception
     */
    public Document decodeSamlPost(String postData) {
        String trimmed = postData.replaceAll("\r\n", "");
        String base64Decoded = new String(Base64.decodeBase64(trimmed));
        
        LOG.debug("Got Saml message via post: \n{}\n", base64Decoded);
        
        try {
            return builder.build(new StringReader(base64Decoded));
        } catch (Exception e) {
            LOG.error("Error unmarshalling saml post", e);
            throw new IllegalArgumentException("Posted SAML isn't valid");
        }
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
