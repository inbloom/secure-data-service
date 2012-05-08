package org.slc.sli.ingestion.referenceresolution;

/**
 *
 *
 *
 */
public interface ReferenceResolutionStrategy {

    public String resolveReference(String referenceName, String referenceId, String enclosingEntityName, String idContent, String interchangeName);
}
