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

package org.slc.sli.modeling.xmi.reader;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xmi.XmiElementName;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the XMI reading utility.
 *
 * @author kmyers
 */
public class XmiReaderTest {

    private XMLStreamReader mockReader = mock(XMLStreamReader.class);
    private XmiAttributeName sampleAttribute = XmiAttributeName.ID;

    @Test
    public void testAssertNotNullHappyPath() {
        Object o = new Object();
        assertTrue(XmiReader.assertNotNull(o) == o);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotNullSadPath() {
        XmiReader.assertNotNull(null);
    }

    @Test
    public void testMatches() {

        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        XmiElementName nonMatchingXmiElementName = XmiElementName.ASSOCIATION_END;
        String elementName = xmiElementName.getLocalName();
        QName matchingQName = new QName(elementName);
        QName nonMatchingQName = new QName("");

        when(mockReader.getName()).thenReturn(matchingQName);
        when(mockReader.getLocalName()).thenReturn(elementName);

        assertTrue(XmiReader.match(xmiElementName, matchingQName));
        assertFalse(XmiReader.match(xmiElementName, nonMatchingQName));

        assertTrue(XmiReader.match(xmiElementName, mockReader));
        assertFalse(XmiReader.match(nonMatchingXmiElementName, mockReader));

        assertTrue(XmiReader.match(matchingQName, mockReader));
        assertFalse(XmiReader.match(nonMatchingQName, mockReader));

    }

    @Test
    public void testGetOccursHappyPath() {
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("1");
        assertTrue(XmiReader.getOccurs(mockReader, sampleAttribute) == Occurs.ONE);

        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("0");
        assertTrue(XmiReader.getOccurs(mockReader, sampleAttribute) == Occurs.ZERO);

        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("-1");
        assertTrue(XmiReader.getOccurs(mockReader, sampleAttribute) == Occurs.UNBOUNDED);
    }

    @Test(expected = AssertionError.class)
    public void testGetOccursSadPath() {

        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("123");
        XmiReader.getOccurs(mockReader, sampleAttribute);
    }

    @Test
    public void testGetIdRefHappyPath() {
        String id = "ID1234567890";
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(id);
        assertTrue(XmiReader.getIdRef(mockReader).toString().equals(id));
    }

    @Test(expected = XmiMissingAttributeException.class)
    public void testGetIdRefSadPath() {
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(null);
        XmiReader.getIdRef(mockReader);
    }

    @Test
    public void testGetIdHappyPath() {
        String id = "ID1234567890";
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(id);
        assertTrue(XmiReader.getId(mockReader).toString().equals(id));
    }

    @Test
    public void testGetBoolean() {

        boolean defaultBoolean;

        defaultBoolean = false;
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("true");
        assertTrue(XmiReader.getBoolean(sampleAttribute, defaultBoolean, mockReader));

        defaultBoolean = true;
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("false");
        assertFalse(XmiReader.getBoolean(sampleAttribute, defaultBoolean, mockReader));

        defaultBoolean = false;
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(null);
        assertFalse(XmiReader.getBoolean(sampleAttribute, defaultBoolean, mockReader));

        defaultBoolean = true;
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(null);
        assertTrue(XmiReader.getBoolean(sampleAttribute, defaultBoolean, mockReader));

    }

    @Test
    public void testCloseQuiet() throws IOException {

        Closeable mockCloseable = mock(Closeable.class);

        final StringBuffer stringBuffer = new StringBuffer();

        PrintStream stdErr = System.err;
        PrintStream myErr = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                stringBuffer.append((char) b);
            }
        });

        //push
        System.setErr(myErr);

        //test successful close
        XmiReader.closeQuiet(mockCloseable);
        assertTrue(stringBuffer.toString().equals(""));

        //pop
        System.setErr(stdErr);
    }

    @Test
    public void testAssertElementNameEqualsStreamReadNameHappyPath() {
        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        String elementName = xmiElementName.getLocalName();

        when(mockReader.getLocalName()).thenReturn(elementName);

        XmiReader.assertName(xmiElementName, mockReader);

    }

    @Test(expected = AssertionError.class)
    public void testAssertElementNameEqualsStreamReadNameSadPath() {
        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        String elementName = XmiElementName.ASSOCIATION_END.getLocalName();

        when(mockReader.getLocalName()).thenReturn(elementName);

        XmiReader.assertName(xmiElementName, mockReader);

    }

    @Test
    public void testAssertQNameEqualsStreamReadNameHappyPath() {
        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        String elementName = xmiElementName.getLocalName();
        QName matchingQName = new QName(elementName);

        when(mockReader.getName()).thenReturn(matchingQName);
        when(mockReader.getLocalName()).thenReturn(elementName);

        XmiReader.assertName(matchingQName, mockReader);

    }

    @Test(expected = AssertionError.class)
    public void testAssertQNameEqualsStreamReadNameSadPath() {
        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        String elementName = xmiElementName.getLocalName();
        QName matchingQName = new QName(elementName);
        QName nonMatchingQName = new QName("");

        when(mockReader.getName()).thenReturn(matchingQName);
        when(mockReader.getLocalName()).thenReturn(elementName);

        XmiReader.assertName(nonMatchingQName, mockReader);

    }

    @Test
    public void testConstructor() {
        new XmiReader();
    }

    @Test
    public void testSkipElement() throws XMLStreamException {
        final List<String> localNames = new ArrayList<String>();
        localNames.add("myLocalName1");
        localNames.add("myLocalName2");
        localNames.add("myLocalName2");
        localNames.add("myLocalName1");

        final List<Integer> eventTypes = new ArrayList<Integer>();
        eventTypes.add(XMLStreamConstants.START_ELEMENT);
        eventTypes.add(XMLStreamConstants.CHARACTERS);
        eventTypes.add(XMLStreamConstants.END_ELEMENT);
        eventTypes.add(XMLStreamConstants.END_ELEMENT);

        when(mockReader.hasNext()).thenReturn(true);
        when(mockReader.getEventType()).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return eventTypes.remove(0);
            }
        });
        when(mockReader.getLocalName()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return localNames.remove(0);
            }
        });

        XmiReader.skipElement(mockReader, false);
    }

    @Test(expected = AssertionError.class)
    public void testSkipElementSadPath1TrueCheck() throws XMLStreamException {

        XmiElementName xmiElementName = XmiElementName.ASSOCIATION;
        String elementName = xmiElementName.getLocalName();
        QName matchingQName = new QName(elementName);

        when(mockReader.getName()).thenReturn(matchingQName);
        XmiReader.skipElement(mockReader, true);
    }

    @Test(expected = AssertionError.class)
    public void testSkipElementSadPath2NoNext() throws XMLStreamException {

        when(mockReader.hasNext()).thenReturn(false);
        XmiReader.skipElement(mockReader, false);
    }

    @Test(expected = AssertionError.class)
    public void testSkipElementSadPath3UnknownEventType() throws XMLStreamException {

        when(mockReader.hasNext()).thenReturn(true);
        when(mockReader.getEventType()).thenReturn(123456789);

        XmiReader.skipElement(mockReader, false);
    }

    @Test(expected = AssertionError.class)
    public void testSkipElementSadPath4LocalNamesDoNotMatch() throws XMLStreamException {

        final List<String> localNames = new ArrayList<String>();
        localNames.add("myLocalName1");
        localNames.add("myLocalName2");
        localNames.add("myLocalName2");

        when(mockReader.hasNext()).thenReturn(true);
        when(mockReader.getEventType()).thenReturn(XMLStreamConstants.END_ELEMENT);
        when(mockReader.getLocalName()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return localNames.remove(0);
            }
        });

        XmiReader.skipElement(mockReader, false);
    }

    @Test
    public void testGetName() {
        String defaultString;

        defaultString = "defString";
        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn("specifiedString");
        assertEquals("specifiedString", XmiReader.getName(mockReader, defaultString, sampleAttribute));

        when(mockReader.getAttributeValue(any(String.class), any(String.class))).thenReturn(null);
        assertEquals(defaultString, XmiReader.getName(mockReader, defaultString, sampleAttribute));
    }

    private Model readModelByFile(String filename) throws FileNotFoundException {

        return XmiReader.readModel(new File(filename));
    }

    private Model readModelByStream(String filename) throws IOException {
        final InputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
        Model model = XmiReader.readModel(inputStream);
        inputStream.close();
        return model;
    }

    @SuppressWarnings("unused")
    private Model readModelByXmlStream(String filename) throws XMLStreamException, FileNotFoundException {

        XMLInputFactory factory = XMLInputFactory.newInstance();

        XMLStreamReader xmlStreamReader = factory.createXMLStreamReader(new FileReader(filename));

        Model model = XmiReader.readModel(xmlStreamReader);

        xmlStreamReader.close();

        return model;
    }

    private Model readModelByName(String filename) throws FileNotFoundException {

        return XmiReader.readModel(filename);
    }

    @Test
    public void testAllPublicMethods() throws XMLStreamException, IOException {

        String filename = "src/test/resources/SLI.xmi";

        Model model1 = this.readModelByFile(filename);
        Model model2 = this.readModelByStream(filename);
        //Model model3 = this.readModelByXmlStream(filename);
        Model model4 = this.readModelByName(filename);


        assertNotNull(model1);
        assertNotNull(model2);
        //assertNotNull(model3);
        assertNotNull(model4);

        assertEquals(model1, model2);
        //assertEquals(model1, model3);
        assertEquals(model1, model4);
    }

}
