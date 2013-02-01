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

package org.slc.sli.modeling.xsd;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author jstokes
 */
public class XsdReaderTest {

    @Test
    public void testReadSchemaUsingFile() throws FileNotFoundException, URISyntaxException {
        final URIResolver mockResolver = mock(URIResolver.class);
        final File testXSD = new File(getClass().getResource("/test.xsd").toURI());
        final XmlSchema schema = XsdReader.readSchema(testXSD, mockResolver);
        testReadSchema(schema);
    }

    @Test
    public void testReadSchemaUsingString() throws URISyntaxException, FileNotFoundException {
        final URIResolver mockResolver = mock(URIResolver.class);
        final String testXSD = new File(getClass().getResource("/test.xsd").toURI()).getAbsolutePath();
        final XmlSchema schema = XsdReader.readSchema(testXSD, mockResolver);
        testReadSchema(schema);
    }

    @Test
    public void testInstantiation() {
        final XsdReader reader = new XsdReader();
        assertNotNull(reader);
    }

    private void testReadSchema(final XmlSchema schema) {
        assertNotNull(schema);
        assertEquals(1, schema.getElements().getCount());
        assertNotNull(schema.getElements().getItem(new QName(schema.getTargetNamespace(), "weaponsType")));
    }
}
