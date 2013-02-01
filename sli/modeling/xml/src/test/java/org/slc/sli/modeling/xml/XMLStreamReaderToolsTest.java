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

package org.slc.sli.modeling.xml;

import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

/**
 * @author chung
 */
public class XMLStreamReaderToolsTest {

    @Test
    public void testCreateObject() {
        @SuppressWarnings("unused")
        XMLStreamReaderTools tools = new XMLStreamReaderTools();
    }

    @Test
    public void testSkipElement() throws FileNotFoundException, XMLStreamException {
        File file = new File(getAbsPath("sample.xml"));
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);

        while (!reader.isStartElement()) {
            reader.next();
        }
        XMLStreamReaderTools.skipElement(reader); // skip everything inside
        assertTrue(reader.isEndElement());
    }

    private String getAbsPath(final String fileName) {
        return getClass().getResource("/" + fileName).getFile();
    }

}
