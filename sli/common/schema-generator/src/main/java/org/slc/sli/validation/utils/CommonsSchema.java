package org.slc.sli.validation.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;

/**
 * Utilities for working with apache commons schema.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class CommonsSchema {
    
    public static void main(String... args) {
        // String xsd = "/xsd/Ed-Fi-Core.xsd";
        // XmlSchema schema = loadSchema(xsd);
        //
        // XmlSchemaType section = schema.getTypeByName("Section");
        
        // System.out.println(listElements(section));
    }
    
    public static XmlSchema loadSchema(String resourcePath) {
        Reader xsd = null;
        try {
            xsd = new InputStreamReader(XsdToAvroSchema.class.getResourceAsStream(resourcePath));
            XmlSchemaCollection col = new XmlSchemaCollection();
            return col.read(xsd, null);
        } finally {
            if (xsd != null) {
                try {
                    xsd.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static List<XmlSchemaElement> listElements(XmlSchema schema) {
        List<XmlSchemaElement> list = new LinkedList<XmlSchemaElement>();
        XmlSchemaObjectTable elements = schema.getElements();
        @SuppressWarnings("rawtypes")
        Iterator names = elements.getNames();
        while (names.hasNext()) {
            QName next = (QName) names.next();
            XmlSchemaObject item = elements.getItem(next);
            list.add((XmlSchemaElement) item);
        }
        return list;
    }
    
}