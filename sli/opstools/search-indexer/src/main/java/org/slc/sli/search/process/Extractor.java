package org.slc.sli.search.process;

import org.quartz.JobExecutionException;

public interface Extractor {
    
    public abstract void execute() throws JobExecutionException;
    
}