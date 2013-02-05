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

package org.slc.sli.modeling.docgen;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test for DocumentationReader class.
 */
public class DocumentationReaderTest {

    private static final String XMI_FILENAME = "src/test/resources/SLI.xmi";
    private static final String DOMAIN_FILENAME = "src/test/resources/domains.xml";


    private static final File XMI_FILE = new File(XMI_FILENAME);
    private static final File DOMAIN_FILE = new File(DOMAIN_FILENAME);

    private ModelIndex modelIndex;

    @Before
    public void before() throws FileNotFoundException {
        this.modelIndex = new DefaultModelIndex(XmiReader.readModel(XMI_FILE));
    }

    @Test
    public void test() throws FileNotFoundException {
        Documentation<Type> resultsByFile = DocumentationReader.readDocumentation(DOMAIN_FILE, this.modelIndex);
        Documentation<Type> resultsByFilename = DocumentationReader.readDocumentation(DOMAIN_FILENAME, this.modelIndex);

        assertEquals(resultsByFile.getDomains().toString(), resultsByFilename.getDomains().toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new DocumentationReader();
    }
}
