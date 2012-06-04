package org.slc.sli.ingestion.xml.idref;

import java.util.concurrent.Callable;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Id Reference Resolution of the future...
 * 
 * @author shalka
 */
public class IdRefResolutionCallable implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionCallable.class);
    
    private IdRefResolutionHandler resolver;    
    private IngestionFileEntry entry;
    
    /**
     * Default constructor for the id reference resolution callable.
     * 
     * @param handler IdRefResolutionHandler to resolve references in ingestion file entries.
     * @param fileEntry ingestion file entry.
     */
    public IdRefResolutionCallable(IdRefResolutionHandler handler, IngestionFileEntry fileEntry) {
        this.resolver = handler;
        this.entry = fileEntry;
    }
    
    /**
     * Entry point of IdRefResolutionCallable.
     */
    @Override
    public Boolean call() throws Exception {
        LOG.info("Starting IdRefResolutionCallable for: " + entry.getFileName());        
        IngestionFileEntry fileEntry = processResolution(entry, entry.getErrorReport());   
        int errorCount = aggregateAndLogResolutionErrors(fileEntry);
        LOG.info("Finished IdRefResolutionCallable for: " + entry.getFileName());
        return (errorCount > 0);
    }
    
    /**
     * Process id reference resolution using the specified handler.
     * 
     * @param fileEntry ingestion file entry.
     * @param errorReport error report.
     * @return ingestion file entry (contains faults and errors).
     */
    private IngestionFileEntry processResolution(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        return resolver.handle(fileEntry, errorReport);
    }
    
    /**
     * Logs errors incurred during id reference resolution.
     * 
     * @param fileEntry ingestion file entry.
     * @return integer representing number of errors during id reference resolution.
     */
    private int aggregateAndLogResolutionErrors(IngestionFileEntry fileEntry) {
        int errorCount = 0;
        for (Fault fault : fileEntry.getFaultsReport().getFaults()) {
            if (fault.isError()) {
                errorCount++;
            }
        }
        return errorCount;
    }
}
