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


package org.slc.sli.modeling.tools.wadl2Doc;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clean up a WADL.
 */
public final class Wadl2Doc {

    private static final Logger LOG = LoggerFactory.getLogger(Wadl2Doc.class);

    public static final String DEFAULT_INPUT_FILENAME = "wadl-original.xml";
    public static final String DEFAULT_OUTPUT_FILENAME = "wadl-clean.xml";

    public Wadl2Doc() {
        throw new UnsupportedOperationException();
    }

    public static void main(final String[] args) {

        String inputFilename = (args.length == 2) ? args[0] : DEFAULT_INPUT_FILENAME;
        String outputFilename = (args.length == 2) ? args[1] : DEFAULT_OUTPUT_FILENAME;

        try {
            final Map<String, String> prefixMappings = new HashMap<String, String>();
            final Application app = WadlReader.readApplication(inputFilename);
            WadlWriter.writeDocument(app, prefixMappings, outputFilename);
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }
}
