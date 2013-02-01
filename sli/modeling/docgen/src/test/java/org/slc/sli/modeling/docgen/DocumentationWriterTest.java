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

import org.junit.Test;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Junit test for DocumentationWriter class.
 */
public class DocumentationWriterTest {

    private static final String XMI_FILENAME = "src/test/resources/SLI.xmi";
    private static final String DOMAIN_FILENAME = "src/test/resources/domains.xml";
    private static final String OUTPUT_FILENAME1 = "output1.txt";
    private static final String OUTPUT_FILENAME2 = "output2.txt";


    private static final File XMI_FILE = new File(XMI_FILENAME);
    private static final File DOMAIN_FILE = new File(DOMAIN_FILENAME);


    @Test
    public void test() throws FileNotFoundException {
        ModelIndex modelIndex = new DefaultModelIndex(XmiReader.readModel(XMI_FILE));
        Documentation<Type> documentation = DocumentationReader.readDocumentation(DOMAIN_FILE, modelIndex);

        DocumentationWriter.writeDocument(documentation, modelIndex, OUTPUT_FILENAME1);
        DocumentationWriter.writeDocument(documentation, modelIndex, new File(OUTPUT_FILENAME2));

        File outputFile1 = new File(OUTPUT_FILENAME1);
        File outputFile2 = new File(OUTPUT_FILENAME2);

        assertTrue(outputFile1.exists());
        if (!outputFile2.exists()) {
            outputFile1.delete();
            fail();
        }

        long outputFile1Length = outputFile1.length();
        long outputFile2Length = outputFile2.length();

        outputFile1.delete();
        outputFile2.delete();

        assertTrue(outputFile1Length == outputFile2Length);

    }


    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new DocumentationWriter();
    }
}
