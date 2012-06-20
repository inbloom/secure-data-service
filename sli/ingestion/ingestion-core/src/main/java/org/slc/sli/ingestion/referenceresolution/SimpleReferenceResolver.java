package org.slc.sli.ingestion.referenceresolution;

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
    public String resolve(String xPath, String content) {
        return content;
    }

}
