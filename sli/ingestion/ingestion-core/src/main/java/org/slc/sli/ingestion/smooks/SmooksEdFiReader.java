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

package org.slc.sli.ingestion.smooks;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XML reader for Smooks that stores the XML file location of neutral records.
 *
 * @author tshewchuk
 *
 */
public class SmooksEdFiReader implements XMLReader {

    private XMLReader xmlReader;
    private SmooksContentHandler smooksContentHandler;

    public SmooksEdFiReader() throws SAXException {
        xmlReader = XMLReaderFactory.createXMLReader();
    }

    @Override
    public ContentHandler getContentHandler() {
        return xmlReader.getContentHandler();
    }

    @Override
    public DTDHandler getDTDHandler() {
        return xmlReader.getDTDHandler();
    }

    @Override
    public EntityResolver getEntityResolver() {
        return xmlReader.getEntityResolver();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return xmlReader.getErrorHandler();
    }

    @Override
    public boolean getFeature(String key) throws SAXNotRecognizedException, SAXNotSupportedException {
        return xmlReader.getFeature(key);
    }

    @Override
    public Object getProperty(String key) throws SAXNotRecognizedException, SAXNotSupportedException {
        return xmlReader.getProperty(key);
    }

    @Override
    public void parse(InputSource inputSource) throws IOException, SAXException {
        xmlReader.parse(inputSource);
    }

    @Override
    public void parse(String string) throws IOException, SAXException {
        xmlReader.parse(string);
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        // Use smook's content handler inside of ours (with our locator!).
        smooksContentHandler = new SmooksContentHandler(contentHandler);
        xmlReader.setContentHandler(smooksContentHandler);
    }

    @Override
    public void setDTDHandler(DTDHandler dtdDHandler) {
        xmlReader.setDTDHandler(dtdDHandler);
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        xmlReader.setEntityResolver(entityResolver);
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        xmlReader.setErrorHandler(errorHandler);
    }

    @Override
    public void setFeature(String key, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlReader.setFeature(key, value);
    }

    @Override
    public void setProperty(String key, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlReader.setProperty(key, value);
    }

    public Locator getLocator() {
        return smooksContentHandler.getDocumentLocator();
    }
}
