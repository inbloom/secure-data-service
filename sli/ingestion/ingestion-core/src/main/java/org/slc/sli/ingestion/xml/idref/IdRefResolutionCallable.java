package org.slc.sli.ingestion.xml.idref;

import java.util.concurrent.Callable;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Id Reference Resolution of the future...
 *
 * @author shalka
 */
@Scope("prototype")
@Component
public class IdRefResolutionCallable implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionCallable.class);

    private final IdRefResolutionHandler resolver;
    private final IngestionFileEntry entry;
    private final Job job;
    private final BatchJobDAO batchJobDao;

    /**
     * Default constructor for the id reference resolution callable.
     *
     * @param fileEntry
     *            ingestion file entry.
     * @param job
     *            batch job.
     * @param handler
     *            IdRefResolutionHandler to resolve references in ingestion file entries.
     */
    public IdRefResolutionCallable(IdRefResolutionHandler resolver, IngestionFileEntry fileEntry, Job job,
            BatchJobDAO batchJobDao) {
        this.resolver = resolver;
        this.entry = fileEntry;
        this.job = job;
        this.batchJobDao = batchJobDao;
    }

    /**
     * Entry point of IdRefResolutionCallable.
     */
    @Override
    public Boolean call() throws Exception {
        LOG.info("Starting IdRefResolutionCallable for: " + entry.getFileName());

        resolver.handle(entry, entry.getErrorReport());

        boolean hasErrors = aggregateAndLogResolutionErrors(entry);

        LOG.info("Finished IdRefResolutionCallable for: " + entry.getFileName());
        return hasErrors;
    }

    /**
     * Logs errors incurred during id reference resolution.
     *
     * @param fileEntry
     *            ingestion file entry.
     * @return integer representing number of errors during id reference resolution.
     */
    private boolean aggregateAndLogResolutionErrors(IngestionFileEntry fileEntry) {
        int errorCount = 0;
        for (Fault fault : fileEntry.getFaultsReport().getFaults()) {
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";

            Error error = Error.createIngestionError(job.getId(), fileEntry.getFileName(),
                    BatchJobStageType.XML_FILE_PROCESSOR.getName(), null, null, null, faultLevel, faultLevel,
                    faultMessage);
            batchJobDao.saveError(error);

            if (fault.isError()) {
                errorCount++;
            }
        }
        return errorCount > 0;
    }
}
