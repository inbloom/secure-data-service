/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.xml.idref;

import java.util.concurrent.Callable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Id Reference Resolution of the future...
 *
 * @author shalka
 */
@Scope("prototype")
@Component
public class IdRefResolutionCallable implements Callable<Boolean> {

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
        boolean hasErrors = false;

        info("Starting IdRefResolutionCallable for: " + entry.getFileName());
        resolver.handle(entry, entry.getErrorReport());

        hasErrors = aggregateAndLogResolutionErrors(entry);
        info("Finished IdRefResolutionCallable for: " + entry.getFileName());

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
