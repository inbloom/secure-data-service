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

import org.junit.Test;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
public class PsmConfigWriterTest {

    @Test
    public void testWriteConfig() throws FileNotFoundException {
        final File out = new File("test_psm.xml");
        @SuppressWarnings("unchecked")
        final PsmConfig<Type> documentation = mock(PsmConfig.class);
        final List<PsmDocument<Type>> configList = new ArrayList<PsmDocument<Type>>();

        final PsmResource resource = new PsmResource("test_resource");
        final PsmCollection psmCollection = new PsmCollection("test_collection");
        final Type mockType = mock(Type.class);
        when(mockType.getName()).thenReturn("");
        final PsmDocument<Type> psmDoc = new PsmDocument<Type>(mockType, resource, psmCollection);

        configList.add(psmDoc);

        when(documentation.getDocuments()).thenReturn(configList);
        final Model model = mock(Model.class);

        PsmConfigWriter.writeConfig(documentation, model, out.getAbsolutePath());
        final ModelIndex mockIndex = mock(ModelIndex.class);

        final Set<ModelElement> mockElementSet = new TreeSet<ModelElement>();
        mockElementSet.add(mock(ClassType.class));
        when(mockIndex.lookupByName(any(QName.class))).thenReturn(mockElementSet);

        PsmConfig<Type> config = PsmConfigReader.readConfig(out.getAbsoluteFile(), mockIndex);
        assertNotNull(config);
        assertEquals("test_resource", config.getDocuments().get(0).getGraphAssociationEndName().getName());
        assertEquals("test_collection", config.getDocuments().get(0).getSingularResourceName().getName());

        out.deleteOnExit();
    }

    @Test
    public void testInit() {
        final PsmConfigWriter psmWriter = new PsmConfigWriter();
        assertNotNull(psmWriter);
    }
}
