package org.slc.sli.search.process;

import java.io.File;

import org.slc.sli.search.entity.IndexEntity.Action;

/**
 * Loads records from a file for indexing
 * @author agrebneva
 *
 */
public interface Loader {
    
    public abstract void processFile(File inFile);
    
    public abstract void processFile(String index, Action action, File inFile);
    
}