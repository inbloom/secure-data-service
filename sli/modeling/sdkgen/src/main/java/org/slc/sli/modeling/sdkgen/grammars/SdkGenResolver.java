package org.slc.sli.modeling.sdkgen.grammars;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * A resolver that currently does nothing.
 */
public final class SdkGenResolver implements URIResolver {

    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        throw new UnsupportedOperationException(getClass().getName() + ".resolveEntity");
    }
}
