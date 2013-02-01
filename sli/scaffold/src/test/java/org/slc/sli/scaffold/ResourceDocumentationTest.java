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

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * @author jstokes
 */
public class ResourceDocumentationTest {
    private ResourceDocumentation rDoc; // class under test

    private DocumentManipulator documentManipulator = new DocumentManipulator();
    private static final String TEST_WADL_LOC = "/test_wadl.wadl";
    private static final String TEST_RESOURCE_LOC = "/test_resources.json";
    private Document testWadl;

    @Before
    public void setup() throws Exception {
        URI wadlUri = this.getClass().getResource(TEST_WADL_LOC).toURI();
        testWadl = documentManipulator.parseDocument(new File(wadlUri));
        rDoc = new ResourceDocumentation(testWadl, TEST_RESOURCE_LOC);
        rDoc.addDocumentation();
    }

    @Test
    public void testAddDocToSubResource() throws Exception {
        Node gradebookEntryDoc = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/resource[@path='{id}/gradebookEntries']/doc").item(0);
        assertNotNull(gradebookEntryDoc);
        assertEquals("test section/{id}/gradebookEntries doc", gradebookEntryDoc.getFirstChild().getNodeValue());
    }

    @Test
    public void testAddDeprecatedToSubResource() throws Exception {
        Node gradebookEntryDV = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/resource[@path='{id}/gradebookEntries']/deprecatedVersion").item(0);
        assertNotNull(gradebookEntryDV);
        assertEquals("v1", gradebookEntryDV.getFirstChild().getNodeValue());

        Node gradebookEntryDR = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/resource[@path='{id}/gradebookEntries']/deprecatedReason").item(0);
        assertNotNull(gradebookEntryDR);
        assertEquals("no good!", gradebookEntryDR.getFirstChild().getNodeValue());
    }

    @Test
    public void testAddDeprecated() throws Exception {
        Node sectionNodeDV = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/deprecatedVersion").item(0);
        assertNotNull(sectionNodeDV);
        assertEquals("v3", sectionNodeDV.getFirstChild().getNodeValue());

        Node sectionNodeDR = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/deprecatedReason").item(0);
        assertNull(sectionNodeDR);
    }

    @Test
    public void testAddDocumentation() throws Exception {
        NodeList section = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']");
        assertEquals(1, section.getLength());

        Node sectionNodeDoc = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/doc").item(0);
        assertNotNull(sectionNodeDoc);
        assertEquals("test /sections doc", sectionNodeDoc.getFirstChild().getNodeValue());
    }

    @Test
    public void testAvailableSince() throws Exception {
        NodeList section = documentManipulator.getNodeList(testWadl, "//resource[@path='v1/sections']");
        assertEquals(1, section.getLength());

        Node sectionNodeAS = documentManipulator.getNodeList(testWadl,
                "//resource[@path='v1/sections']/availableSince").item(0);
        assertNotNull(sectionNodeAS);
        assertEquals("v1", sectionNodeAS.getFirstChild().getNodeValue());
    }
}
