package org.slc.sli.ingestion.referenceresolution;

import java.io.File;

/**
 * Reference resolution strategy.
 *
 * @author okrook
 *
 */
public interface ReferenceResolutionStrategy {

    /**
     * Prepare content for an element referred by xPath.
     *
     * @param xPath XPath for the element
     * @param content Content file that needs to be prepared
     * @return File that holds prepared content
     */
    public File resolve(String xPath, File content);
}
