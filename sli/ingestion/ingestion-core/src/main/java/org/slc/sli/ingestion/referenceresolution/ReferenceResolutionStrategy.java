package org.slc.sli.ingestion.referenceresolution;

/**
 *
 *
 *
 */
public interface ReferenceResolutionStrategy {

    public String resolve(String interchange, String element, String reference, String content);
}
