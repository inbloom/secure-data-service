package org.slc.sli.ingestion.streaming.impl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.streaming.TypeProvider;

/**
 * Provides xsd-based typification services to the parser
 *
 * @author dkornishev
 *
 */
@Component
public class XsdTypeProvider implements TypeProvider {

    private static final Namespace XS_NAMESPACE = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

    // private String schemaLocation = "sliXsd-R1/ComplexTypes-R1.xsd";    // FIXME, should be a property

    @Value("file:${sli.conf}")
    private Resource sliPropsFile;

    private Map<String, Element> complexTypes = new HashMap<String, Element>();
    private Map<String, String> typeMap = new HashMap<String, String>();

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() throws Exception {

        Properties sliProps = PropertiesLoaderUtils.loadProperties(sliPropsFile);
        if ( null == sliProps ) {
                    throw new Exception("Cannot load properties from props file '" + sliPropsFile + "' == ${sli.conf}"); // NOPMD POC code
        }

        // FIXME: file name from properties should be relative to SLI home dir not the current dir
        String curdir = System.getProperty("user.dir");
        String schemaLocation = curdir + "/" + sliProps.getProperty("sli.poc.atma.schema");
        // InputStream schemaStream = ClassLoader.getSystemResourceAsStream(schemaLocation);
        FileInputStream schemaStream = new FileInputStream(schemaLocation);
        if ( null == schemaStream ) {
                    throw new Exception("Cannot open XSD schema '" + schemaLocation + "'"); // NOPMD POC code
        }

        SAXBuilder b = new SAXBuilder();
        Document doc = b.build(schemaStream);
        Iterable<Element> complexTypes = doc.getDescendants(Filters.element("complexType", XS_NAMESPACE));

        for (Element e : complexTypes) {
            this.complexTypes.put(e.getAttributeValue("name"), e);
        }

        Iterable<Element> elements = doc.getDescendants(Filters.element("element", XS_NAMESPACE));

        for (Element e : elements) {
            String type = getType(e);

            this.typeMap.put(e.getAttributeValue("name"), type);
        }
    }

    @Override
    public boolean isComplexType(String elementName) {
        return this.complexTypes.containsKey(elementName) || this.complexTypes.containsKey(this.typeMap.get(elementName));
    }
    
    @Override
    public boolean existsInSchema(String parentName, String name) {
        Element parent = getComplexElement(parentName);
        
        return parent.getChild(name) != null;
    }

    private Element getComplexElement(String parentName) {
        Element parent = this.complexTypes.get(parentName);
        
        if(parent==null) {
            parent = this.complexTypes.get(this.typeMap.get(parentName));
        }
        
        return parent;
    }

    @Override
    public Object convertType(String elementName, String value) {
        String type = this.typeMap.get(elementName);

        Object result=value;
        if (type.equals("xs:date")) {
            result=value;
        } else if (type.equals("xs:boolean")) {
            result=Boolean.parseBoolean(value);
        } else if (type.equals("xs:double")) {
            result=Double.parseDouble(value);
        } else if (type.equals("xs:int")) {
            result=Integer.parseInt(value);
        }

        return result;
    }

    /**
     * Determines if given field is a reference type
     */
    @Override
    public boolean isReference(String elementName) {
        return this.typeMap.get(elementName).equals("reference");

    }


    /**
     * Figures out xsd type of the element Normally taken from the 'type' attribute, in other cases, needs to dig deeper
     *
     * @param e node in the tree
     * @return variable type if available
     */
    private String getType(Element e) {
        String type = e.getAttributeValue("type");

        if (type == null) {
            Element simple = e.getChild("simpleType", XS_NAMESPACE);

            if (simple != null) {
                Element restriction = simple.getChild("restriction", XS_NAMESPACE);

                if (restriction != null) {
                    type = restriction.getAttributeValue("base");
                }
            }
        }
        return type;
    }
}
