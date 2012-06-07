package org.slc.sli.modeling.xmigen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * {@link URIResolver} implementation that resolves <code>schemaLocation</code> using a parent
 * {@link File}.
 */
public final class ParentFileURIResolver implements URIResolver {

    private final File parentFile;

    public ParentFileURIResolver(final File parentFile) {
        if (parentFile == null) {
            throw new NullPointerException("parentFile");
        }
        this.parentFile = parentFile;
    }

    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        try {
            final File file = new File(parentFile, schemaLocation);
            final InputStream byteStream = new BufferedInputStream(new FileInputStream(file));
            return new InputSource(byteStream);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
