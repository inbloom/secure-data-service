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

package org.slc.sli.modeling.sdkgen.grammars.xsd;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class SdkGenGrammarsWrapperTest {
    @Test
    public void testGetElement() throws Exception {
        final XmlSchemaElement expected = mock(XmlSchemaElement.class);
        final List<XmlSchema> schemaList = new ArrayList<XmlSchema>();

        final XmlSchema schema1 = mock(XmlSchema.class);
        when(schema1.getElementByName(any(QName.class))).thenReturn(null);

        final XmlSchema schema2 = mock(XmlSchema.class);
        when(schema2.getElementByName(any(QName.class))).thenReturn(expected);

        schemaList.add(schema1);
        schemaList.add(schema2);

        final SdkGenGrammarsWrapper wrapper = new SdkGenGrammarsWrapper(schemaList);

        assertEquals(expected, wrapper.getElement(new QName("Test")));
    }
}
