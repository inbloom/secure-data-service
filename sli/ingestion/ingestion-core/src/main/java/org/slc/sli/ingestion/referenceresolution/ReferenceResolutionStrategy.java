package org.slc.sli.ingestion.referenceresolution;

import java.io.File;

public interface ReferenceResolutionStrategy {
    public String resolveReference(String referenceName, String referenceId, String enclosingEntityName, File xmlFile, String interchangeName);
}
