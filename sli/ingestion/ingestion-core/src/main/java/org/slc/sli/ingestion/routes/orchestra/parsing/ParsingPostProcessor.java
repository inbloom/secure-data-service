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

package org.slc.sli.ingestion.routes.orchestra.parsing;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Processing step for cleanup after parsing aggregation
 * @author ablum
 *
 */
@Component
public class ParsingPostProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(ParsingPostProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        FileEntryWorkNote workNote = exchange.getIn().getBody(FileEntryWorkNote.class);
        String jobId = workNote.getBatchJobId();
        exchange.getIn().setHeader("jobId", jobId);

        IngestionFileEntry fileEntry = workNote.getFileEntry();

        File zipFile = new File(fileEntry.getFileZipParent());

        if (!zipFile.delete()) {
            LOG.debug("Failed to delete: {}", zipFile.getPath());
        }

    }

}
