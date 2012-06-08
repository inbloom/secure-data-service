package org.slc.sli.modeling.xmigen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

public final class Xsd2XmlResolver implements URIResolver {

    private final File path;

    public Xsd2XmlResolver(final File path) {
        this.path = path;
    }

    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        try {
            final File file = new File(path, schemaLocation);
            final InputStream byteStream = new BufferedInputStream(new FileInputStream(file));
            return new InputSource(byteStream);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
