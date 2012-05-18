package org.slc.sli.sandbox.idp.saml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.security.saml2.XmlSignatureHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class SamlResponseComposerTest {
    
    @Mock
    XmlSignatureHelper sigHelper;
    
    @InjectMocks
    SamlResponseComposer composer = new SamlResponseComposer();
    
    static final String SAMLP_NS = "urn:oasis:names:tc:SAML:2.0:protocol";
    static final String SAML_NS = "urn:oasis:names:tc:SAML:2.0:assertion";
    
    @Test
    public void testComponseResponse() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            KeyException, TransformerException, MarshalException, XMLSignatureException, SAXException, IOException,
            ParserConfigurationException {
        
        Mockito.when(sigHelper.signSamlAssertion(Mockito.any(Document.class))).thenAnswer(new Answer<Document>() {
            
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return (Document) invocation.getArguments()[0];
            }
        });
        
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "User Name");
        attributes.put("tenant", "tenantId");
        attributes.put("edorg", "NC-edorg");
        attributes.put("adminRealm", "myrealm");
        String encodedXml = composer.componseResponse("dest", "issuer", "requestId", "userId", attributes,
                Arrays.asList("role1", "role2"));
        
        String xml = new String(Base64.decodeBase64(encodedXml), "UTF8");
        
        Mockito.verify(sigHelper).signSamlAssertion(Mockito.any(Document.class));
        
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(true);
        Document doc = fact.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        
        assertEquals("dest", doc.getDocumentElement().getAttribute("Destination"));
        assertEquals("requestId", doc.getDocumentElement().getAttribute("InResponseTo"));
        assertTrue(hasAttributeValue(doc, "userName", "User Name"));
        assertTrue(hasAttributeValue(doc, "userId", "userId"));
        assertTrue(hasAttributeValue(doc, "roles", "role1"));
        assertTrue(hasAttributeValue(doc, "roles", "role2"));
        assertTrue(hasAttributeValue(doc, "tenant", "tenantId"));
        assertTrue(hasAttributeValue(doc, "edorg", "NC-edorg"));
        assertTrue(hasAttributeValue(doc, "adminRealm", "myrealm"));
        assertEquals("issuer", xpathOne(doc, "/samlp:Response/saml:Issuer").getTextContent());
        assertTrue(xpathOne(doc, "/samlp:Response/@IssueInstant").getTextContent().matches(
                "\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ"));
        assertTrue(xpathOne(doc, "/samlp:Response/saml:Assertion/@IssueInstant").getTextContent().matches(
                "\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ"));
        assertTrue(!xpathOne(doc, "/samlp:Response/@ID").getTextContent().equals("__RESPONSE_ID__"));
        assertTrue(!xpathOne(doc, "/samlp:Response/saml:Assertion/@ID").getTextContent().equals("__ASSERTION_ID__"));
        assertTrue(!xpathOne(doc, "/samlp:Response/@Destination").getTextContent().equals("__DESTINATION__"));
    }
    
    private static boolean hasAttributeValue(Document doc, String attrType, String attrValue) {
        NodeList nodes = xpathList(doc, "/samlp:Response/saml:Assertion/saml:AttributeStatement/saml:Attribute[@Name='"
                + attrType + "']/saml:AttributeValue");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String text = node.getTextContent();
            if (attrValue.equals(text)) {
                return true;
            }
        }
        return false;
        
    }
    
    private static NodeList xpathList(Document doc, String xpath) {
        try {
            XPath xpather = XPathFactory.newInstance().newXPath();
            xpather.setNamespaceContext(new NamespaceContext() {
                
                @Override
                public String getNamespaceURI(String prefix) {
                    if ("samlp".equals(prefix)) {
                        return SAMLP_NS;
                    } else if ("saml".equals(prefix)) {
                        return SAML_NS;
                    }
                    return null;
                }
                
                @Override
                public String getPrefix(String namespaceURI) {
                    return null;
                }
                
                @SuppressWarnings("rawtypes")
                @Override
                public Iterator getPrefixes(String namespaceURI) {
                    return null;
                }
            });
            XPathExpression xpathExpr = xpather.compile(xpath);
            NodeList nodes = (NodeList) xpathExpr.evaluate(doc, XPathConstants.NODESET);
            return nodes;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Node xpathOne(Document doc, String xpath) {
        NodeList list = xpathList(doc, xpath);
        assertEquals(1, list.getLength());
        return list.item(0);
    }
}
