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
package org.slc.sli.ingestion;

import java.io.Serializable;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * A unit of work for parsing orchestration, based on an IngestionFileEntry
 * @author ablum
 *
 */
public final class FileEntryWorkNote implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String batchJobId;
    private final IngestionFileEntry fileEntry;
    private final int batchSize;

    public FileEntryWorkNote(String batchJobId, IngestionFileEntry fileEntry, int batchSize) {
        this.batchJobId = batchJobId;
        this.fileEntry = fileEntry;
        this.batchSize = batchSize;
    }

    /**
     * Create a simple FileEntryWorkNote, note part of any batch.
     *
     * @param batchJobId
     * @param ingestionStagedEntity
     * @return
     */
    public static FileEntryWorkNote createSimpleWorkNote(String batchJobId) {
        return new FileEntryWorkNote(batchJobId, null, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + batchSize;
        result = prime * result + ((fileEntry == null) ? 0 : fileEntry.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "FileEntryWorkNote [batchJobId=" + batchJobId + ", batchSize=" + batchSize + ", ingestionFileEntry=" + "fileEntry]";
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public IngestionFileEntry getFileEntry() {
        return fileEntry;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
