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

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;

import javax.xml.namespace.QName;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class SdkGenElementWrapperTest {

    private SdkGenElementWrapper wrapper;

    @Mock
    private XmlSchemaElement element;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        wrapper = new SdkGenElementWrapper(element);
    }

    @Test
    public void testGetName() throws Exception {
        QName name = new QName("Test");
        when(element.getQName()).thenReturn(name);

        assertEquals(name, wrapper.getName());
    }

    @Test
    public void testGetType() throws Exception {
        XmlSchemaType schemaType = mock(XmlSchemaComplexType.class);
        when(element.getSchemaType()).thenReturn(schemaType);
        SdkGenType expected = new SdkGenTypeWrapper(schemaType);

        assertEquals(expected.toString(), wrapper.getType().toString());
    }

    @Test
    public void testToString() throws Exception {
        QName name = new QName("Test");
        when(element.getQName()).thenReturn(name);

        String expected = "{name : Test}";
        assertEquals(expected, wrapper.toString());
    }
}
