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
package org.slc.sli.ingestion.parser;

import javax.xml.stream.XMLEventReader;

import org.xml.sax.ErrorHandler;

/**
 * An XML event reader that can be attached to a Validator to parse as it traverses the document.
 *
 * Additionally implements ErrorHandler so that the parsing can be aware of validation errors.
 *
 * @author dduran
 *
 */
public interface RecordParser extends XMLEventReader, ErrorHandler {

    /**
     * Set the underlying XMLEventReader.
     *
     * @param parentReader
     */
    void setParent(XMLEventReader parentReader);

    /**
     * Add to the list of visitors that are notified upon completion of parsing a record.
     *
     * @param recordVisitor
     */
    void addVisitor(RecordVisitor recordVisitor);
}
