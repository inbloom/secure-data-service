package org.slc.sli.scaffold.semantics;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathException;

import org.slc.sli.scaffold.DocumentManipulator;
import org.slc.sli.scaffold.DocumentManipulatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Adds elements to the existing wadl to include links to the generated 
 * database schema documents.
 * 
 * @author jstokes
 *
 */
public abstract class ResourceDocumenter {
    
    private static final String XPATH = 
            "//ns2:application/ns2:resources/ns2:resource[@path='$RESOURCE_NAME']/ns2:method[@name='GET']/ns2:response/ns2:representation";
    private static final String BASE_URL = 
            "http://ec2-107-20-87-141.compute-1.amazonaws.com:8080/view/API/job/domain/ws/sli/common/domain/Documentation/html/";
    
    @SuppressWarnings("serial")
    private static final Map<String, String> RESOURCE_MAP = new HashMap<String, String>() {
        {
            put("v1/schools", "EducationalOrganization_xsd.html#school");
            put("v1/students", "StudentAcademicRecord_xsd.html");
        } 
    };
    
    /**
     * Adds links for each resource to the generated wadl 
     * @param wadlDoc The wadl document to add schema links to
     * @throws XPathException 
     * @throws DocumentManipulatorException
     */
    public static void addResourceMerge(Document wadlDoc) throws XPathException, DocumentManipulatorException {
        for (Map.Entry<String, String> entry : RESOURCE_MAP.entrySet()) {
            addResource(wadlDoc, entry);
        }
    }

    private static void addResource(Document wadlDoc, Map.Entry<String, String> entry) throws XPathException {
        DocumentManipulator docMan = new DocumentManipulator();
        docMan.init();
        NodeList nodeList = docMan.getNodeList(wadlDoc, getXpathForResource(entry.getKey()));
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element elem = (Element) nodeList.item(i);
            elem.setAttribute("element", "Schema");
            elem.setAttribute("href", BASE_URL + entry.getValue());
        }
    }
    
    private static String getXpathForResource(String resourceName) {
        return XPATH.replace("$RESOURCE_NAME", resourceName);
    }
    
}
