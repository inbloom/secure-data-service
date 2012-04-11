package org.slc.sli.sandbox.idp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slc.sli.sandbox.idp.saml.XmlSignatureHelper;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class LoginService {
    
    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
    
    @Autowired
    XmlSignatureHelper signer;
    
    // @Value("${sli.idp.response-location}")
    String destination = "http://local.slidev.org:8080/api/rest/saml/sso/post";
    String issuer = "http://local.slidev.org:8082/mock-idp";
    
    String roleTemplate = "<saml:AttributeValue xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='xs:string'>__ROLE__</saml:AttributeValue>";
    
    public URI login(User user, List<String> roles, AuthRequests.Request requestInfo) {
        LOG.info("Login for user: {} roles: {} inResponseTo: {} destination: {}", new Object[] { user.getId(), roles,
                requestInfo.getRequestId(), destination });
        
        String template;
        try {
            template = IOUtils.toString(this.getClass().getResourceAsStream("/samlResponseTemplate.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        template = template.replace("__RESPONSE_ID__", UUID.randomUUID().toString());
        template = template.replace("__ASSERTION_ID__", UUID.randomUUID().toString());
        template = template.replace("__REQUEST_ID__", requestInfo.getRequestId());
        template = template.replace("__ISSUE_INSTANT__", currentTimeUTC());
        template = template.replace("__DESTINATION__", destination);
        template = template.replace("__ISSUER__", issuer);
        template = template.replace("__USER_ID__", user.getId());
        template = template.replace("__USER_NAME__", user.getUserName());
        
        StringBuilder buf = new StringBuilder();
        for (String role : roles) {
            buf.append(roleTemplate.replace("__ROLE__", role));
        }
        template = template.replace("__ROLE_ELEMENTS__", buf.toString());
        
        InputSource stringSource = new InputSource();
        stringSource.setCharacterStream(new StringReader(template));
        String signedXml;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            Document unsignedDoc = docFactory.newDocumentBuilder().parse(stringSource);
            Document signedDoc = signer.signSamlAssertion(unsignedDoc);
            
            // any transforms (indentation, etc) will break the XML Signatures. No touch!
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            trans.transform(new DOMSource(signedDoc), new StreamResult(bos));
            signedXml = new String(bos.toByteArray(), "UTF8");
            
        } catch (SAXException e) {
            // holy checked exception list, Batman!
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (MarshalException e) {
            throw new RuntimeException(e);
        } catch (XMLSignatureException e) {
            throw new RuntimeException(e);
        }
        
        LOG.debug("signed xml: {}", new Object[] { signedXml });
        
        URI redirectUri = sendSamlResponse(signedXml, this.destination);
        
        return redirectUri;
    }
    
    private static URI sendSamlResponse(String samlResponse, String destination) {
        try {
            String encodedSamlResponse = Base64.encodeBase64String(samlResponse.getBytes("UTF-8"));
            
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(destination);
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("SAMLResponse",
                    encodedSamlResponse)), "UTF-8");
            
            post.setEntity(data);
            HttpResponse response = httpclient.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (!(status == 307 || status == 301 || status == 302)) {
                throw new RuntimeException("Expected redirect, recieved: " + status);
            }
            Header location = response.getFirstHeader("Location");
            URI redirectUri = URI.create(location.getValue());
            return redirectUri;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String currentTimeUTC() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }
}
