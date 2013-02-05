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

package org.slc.sli.modeling.tools.xmi2Psm;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.index.ModelIndex;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class PsmConfigReaderTest {

    private URI docUri;
    private ModelIndex mockIndex;

    @Before
    public void setup() throws URISyntaxException {
        docUri = getClass().getResource("/test_documents.xml").toURI();
        mockIndex = mock(ModelIndex.class);
        final Set<ModelElement> mockElementSet = new TreeSet<ModelElement>();
        mockElementSet.add(mock(ClassType.class));
        when(mockIndex.lookupByName(any(QName.class))).thenReturn(mockElementSet);
    }

    @Test
    public void testReadConfig() throws FileNotFoundException {
        final File docFile = new File(docUri);
        final PsmConfig config = PsmConfigReader.readConfig(docFile, mockIndex);
        asserts(config);
    }

    @Test
    public void testReadConfigString() throws FileNotFoundException {
        final File docFile = new File(docUri);
        final String docLoc = docFile.getAbsolutePath();
        final PsmConfig config = PsmConfigReader.readConfig(docLoc, mockIndex);
        asserts(config);
    }

    @Test
    public void testReadConfigStream() throws FileNotFoundException {
        final InputStream inputStream = getClass().getResourceAsStream("/test_documents.xml");
        final PsmConfig config = PsmConfigReader.readConfig(inputStream, mockIndex);
        asserts(config);
    }

    @Test
    public void testInitialization() {
        final PsmConfigReader reader = new PsmConfigReader();
        assertNotNull(reader);
    }

    private void asserts(final PsmConfig config) {
        assertNotNull(config);
        assertEquals(1, config.getDocuments().size());
        final PsmDocument doc = (PsmDocument) config.getDocuments().get(0);

        assertNotNull(doc);
        assertEquals("academicHonors", doc.getGraphAssociationEndName().getName());
        assertEquals("academicHonor", doc.getSingularResourceName().getName());
    }
}
