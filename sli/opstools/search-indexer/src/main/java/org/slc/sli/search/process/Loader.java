package org.slc.sli.search.process;

import java.io.File;

/**
 * Loads records from a file for indexing
 * @author agrebneva
 *
 */
public interface Loader {
    
    public abstract void processFile(File inFile);
    
}