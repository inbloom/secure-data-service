package org.slc.sli.sandbox.idp.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Decodes SAMLRequests and parses out the relevant information.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class SamlRequestDecoder {
    
    /**
     * Holds saml request info
     */
    public static class SamlRequest {
        private final String destination;
        private final String id;
        
        SamlRequest(String destination, String id) {
            this.destination = destination;
            this.id = id;
        }
        
        public String getDestination() {
            return destination;
        }
        
        public String getId() {
            return id;
        }
    }
    
    public SamlRequest decode(String encodedSamlRequest) {
        byte[] decodedCompressed = Base64.decodeBase64(encodedSamlRequest);
        Inflater inflater = new Inflater(true);
        InflaterInputStream xmlInputStream = new InflaterInputStream(new ByteArrayInputStream(decodedCompressed),
                inflater);
        Document doc;
        
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(xmlInputStream);
            
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element element = doc.getDocumentElement();
        String destination = element.getAttribute("Destination");
        String id = element.getAttribute("ID");
        if (destination == null) {
            throw new IllegalArgumentException("No Destination attribute on AuthnRequest.");
        }
        if (id == null) {
            throw new IllegalArgumentException("No ID attribute on AuthnRequest.");
        }
        
        return new SamlRequest(destination, id);
    }
}
