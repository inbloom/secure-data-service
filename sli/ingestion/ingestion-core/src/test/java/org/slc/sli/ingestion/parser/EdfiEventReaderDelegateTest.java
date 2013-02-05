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
package org.slc.sli.ingestion.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.XMLConstants;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EdfiEventReaderDelegateTest {

    public static final Logger LOG = LoggerFactory.getLogger(EdfiEventReaderDelegateTest.class);

    @Autowired
    EdfiEventReaderDelegate edfiReader;

    @Test
    @Ignore
    public void testParsing() throws Throwable {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

        String schemaLocation = "/Users/dduran/work/SLI/sli/edfi-schema/src/main/resources/edfiXsd-SLI/SLI-Interchange-StudentDiscipline.xsd";
        Schema schema = getSchema(schemaLocation);

        String xmlLocation = "/Users/dduran/work/SLI/sli/acceptance-tests/test/features/ingestion/test_data/MediumSampleDataSet/InterchangeStudentDiscipline.xml";
        EdfiEventReaderDelegate edfiReader = createReader(xmlLocation);

        Validator validator = schema.newValidator();
        validator.setErrorHandler(edfiReader);

        validator.validate(new StAXSource(edfiReader));
    }

    private Schema getSchema(String schemaLocation) throws SAXException {

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(schemaLocation));
        return schema;
    }

    private EdfiEventReaderDelegate createReader(String xmlLocation) throws FileNotFoundException, XMLStreamException,
            FactoryConfigurationError {

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(xmlLocation));
        edfiReader.setParent(reader);
        return edfiReader;
    }

}
