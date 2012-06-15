package org.slc.sli.ingestion.referenceresolution;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.util.LogUtil;

/**
 *
 * Class to resolve simple references to extended references within ingested XML files.
 *
 * @author mpatel
 *
 */
public class SimpleReferenceResolver implements ReferenceResolutionStrategy {
    public static final Logger LOG = LoggerFactory.getLogger(SimpleReferenceResolver.class);

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
            LogUtil.debug(LOG, "Error while resolving a reference for : " + xPath, e);

            return;
        }
    }

}
