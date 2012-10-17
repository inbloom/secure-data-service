package org.slc.sli.search.process;

import org.slc.sli.search.entity.IndexEntity.Action;

public interface Extractor {
    
    public abstract void execute(Action action);
    
}