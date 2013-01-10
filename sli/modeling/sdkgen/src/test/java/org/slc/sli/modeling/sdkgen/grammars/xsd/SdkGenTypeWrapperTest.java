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

import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author jstokes
 */
public class SdkGenTypeWrapperTest {

    private SdkGenTypeWrapper sdkGenTypeWrapper;

    @Mock
    private XmlSchemaComplexType mockSchemaType;

    @Before
    public void setup() {
        initMocks(this);
        sdkGenTypeWrapper = new SdkGenTypeWrapper(mockSchemaType);
    }

    @Test
    public void testToString() throws Exception {
        final String expected = "{}";

        assertEquals(expected, sdkGenTypeWrapper.toString());
    }

    @Test
    public void testSdkGenDive() throws Exception {
        final XmlSchemaObjectCollection xmlSchemaObjectCollection = mock(XmlSchemaObjectCollection.class);
        final XmlSchemaChoice xmlSchemaElement = mock(XmlSchemaChoice.class);
        when(xmlSchemaObjectCollection.getCount()).thenReturn(1);

        when(xmlSchemaObjectCollection.getItem(0)).thenReturn(xmlSchemaElement);

        assertTrue(SdkGenTypeWrapper.sdkGenDive(xmlSchemaObjectCollection, 0) instanceof SdkGenChoiceTypeWrapper);
    }

    @Test
    public void testSdkGenDive2() throws Exception {
        final XmlSchemaType xmlSchemaType = mock(XmlSchemaType.class);
        assertEquals(null, SdkGenTypeWrapper.sdkGenDive(xmlSchemaType, 7));
    }
}
