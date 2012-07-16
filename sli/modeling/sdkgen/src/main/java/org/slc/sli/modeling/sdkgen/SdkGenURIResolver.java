package org.slc.sli.modeling.sdkgen;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

public final class SdkGenURIResolver implements URIResolver {
    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        throw new UnsupportedOperationException(getClass() + ".resolveEntity()");
    }
}
