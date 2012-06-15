package org.slc.sli.ingestion.referenceresolution;

import java.io.InputStream;
import java.io.OutputStream;

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
     * @return InputStream that holds prepared content
     */
    public boolean resolve(String xPath, InputStream content, OutputStream converedContent);
}
