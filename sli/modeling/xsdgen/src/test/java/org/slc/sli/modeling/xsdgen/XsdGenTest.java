/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.modeling.xsdgen;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.junit.Test;
import org.slc.sli.modeling.xsd.XsdReader;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author jstokes
 */
public class XsdGenTest {

    @Test
    public void testXsdGen() throws IOException {
        final File outFile = new File("test_sli.xsd");
        if (!outFile.exists()) {
            if (outFile.createNewFile()) {
                // file created
            } else {
                fail("failed to create temp file " + outFile.getName());
            }
        }

        final String folder = getFolder(outFile);
        final String file = outFile.getName();

        final String[] strArr = new String[] {
                "--documentFile", getAbsPath("test_doc.xml"),
                "--xmiFile", getAbsPath("test_sli.xmi"),
                "--plugInName", "org.slc.sli.modeling.xsdgen.PluginForREST",
                "--outFolder", folder,
                "--outFile", file
        };

        XsdGen.main(strArr);

        final URIResolver mockResolver = mock(URIResolver.class);
        final XmlSchema schema = XsdReader.readSchema(outFile.getAbsoluteFile(), mockResolver);

        assertNotNull(schema);

        assertEquals(2, schema.getElements().getCount());
        assertNotNull(schema.getElementByName("assessmentList"));
        assertNotNull(schema.getElementByName("assessment"));

        outFile.deleteOnExit();
    }

    @Test
    public void testInit() {
        XsdGen xsdGen = new XsdGen();
        assertNotNull(xsdGen);
    }

    private String getAbsPath(final String fileName) {
        return getClass().getResource("/" + fileName).getFile();
    }

    private String getFolder(final File outFile) {
        return outFile.getAbsolutePath().replace(outFile.getName(), "");
    }
}
