package org.slc.sli.sandbox.idp.saml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
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

@Component
public class SamlRequestDecoder {
    
    /**
     * Holds saml request info
     */
    public static class SamlRequest {
        private final String destination;
        private final String id;
        private final String assertionUrl;
        
        private SamlRequest(String destination, String id, String assertionUrl) {
            this.destination = destination;
            this.id = id;
            this.assertionUrl = assertionUrl;
        }
        
        public String getDestination() {
            return destination;
        }
        
        public String getId() {
            return id;
        }
        
        public String getAssertionUrl() {
            return assertionUrl;
        }
    }
    
    public SamlRequest decode(String encodedSamlRequest) {
        byte[] decodedCompressed = Base64.decodeBase64(encodedSamlRequest);
        Inflater inflater = new Inflater(true);
        InflaterInputStream xmlInputStream = new InflaterInputStream(new ByteArrayInputStream(decodedCompressed),
                inflater);
        Document doc;
        
        // try {
        // BufferedReader reader = new BufferedReader(new InputStreamReader(xmlInputStream,
        // "UTF8"));
        // String line;
        // StringBuilder buf = new StringBuilder();
        // while ((line = reader.readLine()) != null) {
        // buf.append(line).append("\n");
        // }
        // System.err.println(buf.toString());
        // } catch (UnsupportedEncodingException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // xmlInputStream = new InflaterInputStream(new ByteArrayInputStream(decodedCompressed),
        // inflater);
        
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
        String assertionUrl = element.getAttribute("AssertionConsumerServiceURL");
        if (destination == null) {
            throw new IllegalArgumentException("No Destination attribute on AuthnRequest.");
        }
        if (id == null) {
            throw new IllegalArgumentException("No ID attribute on AuthnRequest.");
        }
        if (assertionUrl == null) {
            throw new IllegalArgumentException("No AssertionConsumerServiceURL attribute on AuthnRequest.");
        }
        
        return new SamlRequest(destination, id, assertionUrl);
    }
    
    public static void main(String... args) throws UnsupportedEncodingException, IOException {
        String xml = "<test></test>";
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(bos, def);
        dos.write(xml.getBytes("UTF8"));
        dos.finish();
        dos.close();
        
        String result = Base64.encodeBase64String(bos.toByteArray());
        @SuppressWarnings("deprecation")
        String urlEncodedResult = URLEncoder.encode(result);
        System.out.println(urlEncodedResult);
    }
}
