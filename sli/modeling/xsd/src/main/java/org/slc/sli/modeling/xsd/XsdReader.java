package org.slc.sli.modeling.xsd;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
