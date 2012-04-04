package org.slc.sli.modeling.xsd2xmi.xsd;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.xml.sax.InputSource;

/**
 * Reads from a file name or {@link InputStream} to give an {@link XmlSchema).
 */
public final class XsdReader {
    
    public static final XmlSchema readSchema(final String fileName) throws FileNotFoundException {
        final FileInputStream istream = new FileInputStream(fileName);
        try {
            return readSchema(istream);
        } finally {
            closeQuiet(istream);
        }
    }
    
    private static final XmlSchema readSchema(final InputStream istream) {
        final XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
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
