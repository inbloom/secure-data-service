package org.slc.sli.ingestion.referenceresolution;


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
     * @param contentToResolve Content file that needs to be prepared
     * @return File that holds prepared content
     */
    public String resolve(String xPath, String contentToResolve);
}
