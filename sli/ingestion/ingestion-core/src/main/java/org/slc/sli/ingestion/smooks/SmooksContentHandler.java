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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Content handler for Smooks XMLReader that stores the XML file location of neutral records.
 *
 * @author tshewchuk
 *
 */
public class SmooksContentHandler implements ContentHandler {

     private Locator locator;
     private ContentHandler contentHandler;

     public SmooksContentHandler(ContentHandler contentHandler) {
         this.contentHandler = contentHandler;
     }

     @Override
     public void setDocumentLocator(Locator locator) {
         this.locator = locator;
     }

     public Locator getDocumentLocator() {
         return this.locator;
     }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        contentHandler.characters(arg0, arg1, arg2);
    }

    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        contentHandler.endElement(arg0, arg1, arg2);
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
        contentHandler.endPrefixMapping(arg0);
    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        contentHandler.ignorableWhitespace(arg0, arg1, arg2);

    }

    @Override
    public void processingInstruction(String arg0, String arg1) throws SAXException {
        contentHandler.processingInstruction(arg0, arg0);
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        contentHandler.skippedEntity(arg0);
    }

    @Override
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
        contentHandler.startElement(arg0, arg1, arg2, arg3);
    }

    @Override
    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        contentHandler.startPrefixMapping(arg0, arg1);
    }
}
