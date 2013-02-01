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


package org.slc.sli.modeling.xsd;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * Reads from a {@link File}, fileName or {@link InputStream} to give an {@link XmlSchema).
 */
public final class XsdReader {

    public static final XmlSchema readSchema(final String fileName, final URIResolver schemaResolver)
            throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readSchema(istream, schemaResolver);
        } finally {
            closeQuiet(istream);
        }
    }

    public static final XmlSchema readSchema(final File file, final URIResolver schemaResolver)
            throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readSchema(istream, schemaResolver);
        } finally {
            closeQuiet(istream);
        }
    }

    private static final XmlSchema readSchema(final InputStream istream, final URIResolver schemaResolver) {
        final XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        schemaCollection.setSchemaResolver(schemaResolver);
        return schemaCollection.read(new InputSource(istream), null);
    }

    private static final void closeQuiet(final Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }
}
