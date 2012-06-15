package org.slc.sli.ingestion.referenceresolution;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 *
 * Class to resolve simple references to extended references within ingested XML files.
 *
 * @author mpatel
 *
 */
public class SimpleReferenceResolver implements ReferenceResolutionStrategy {

    /**
     * Main method of the extended reference resolver.
     *
     * @param interchange
     *            Interchange containing reference/target pair.
     * @param element
     *            Entity containing simple reference.
     * @param reference
     *            Simple reference to be expanded to an extended reference.
     * @param content
     *            Body of target entity to be translated to an extended reference.
     *
     * @return String
     *         Extended reference XML text body, or null, if unresolved.
     */
    @Override
    public void resolve(String xPath, InputStream content, OutputStream convertedContext) {
        try {
            IOUtils.copyLarge(content, convertedContext);
        } catch (IOException e) {
            return;
        }
    }

}
