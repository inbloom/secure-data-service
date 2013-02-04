/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.scaffold;

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
