package org.slc.sli.modeling.tools.xsd2xmi.cmdline;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

public final class Xsd2XmlResolver implements URIResolver {
    
    private final String path;
    
    public Xsd2XmlResolver(final String path) {
        this.path = path;
    }
    
    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        try {
            final InputStream byteStream = new BufferedInputStream(new FileInputStream(path.concat(schemaLocation)));
            return new InputSource(byteStream);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
