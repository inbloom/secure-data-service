package org.slc.sli.scaffold;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.xpath.XPathException;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for DocumentManipulator
 *
 * @author srupasinghe
 */
public class DocumentManipulatorTest {
    DocumentManipulator handler = new DocumentManipulator(); //class under test

    @Before
    public void setup() {
        handler.init();
    }

    @Test(expected = DocumentManipulatorException.class)
    public void testParseDocumentEmptyFile() throws DocumentManipulatorException {
        handler.parseDocument(new File(""));
    }

    @Test
    public void testParseDocument() throws DocumentManipulatorException, URISyntaxException {
        URL url = this.getClass().getResource("/sample.xml");

        Document doc = handler.parseDocument(new File(url.toURI()));

        assertNotNull("Document should not be null", doc);
    }

    @Test
    public void testGetNodeList() throws DocumentManipulatorException, URISyntaxException, XPathException {
        URL url = this.getClass().getResource("/sample.xml");
        String expression = "//resources/resource[doc='Aggregation Resource']/method";

        Document doc = handler.parseDocument(new File(url.toURI()));
        NodeList list = handler.getNodeList(doc, expression);

        assertNotNull("list shoud not be null", list);
    }
}
