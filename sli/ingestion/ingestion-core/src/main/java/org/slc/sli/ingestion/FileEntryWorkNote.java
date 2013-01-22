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
public class FileEntryWorkNote extends SLIWorkNote implements Serializable {
    private static final long serialVersionUID = 638837959304251101L;

    private final IngestionFileEntry fileEntry;

    public FileEntryWorkNote(String batchJobId, String tenantId, IngestionFileEntry fileEntry) {
        super(batchJobId, tenantId);

        this.fileEntry = fileEntry;
    }

    public IngestionFileEntry getFileEntry() {
        return fileEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fileEntry == null) ? 0 : fileEntry.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileEntryWorkNote other = (FileEntryWorkNote) obj;
        if (fileEntry == null) {
            if (other.fileEntry != null) {
                return false;
            }
        } else if (!fileEntry.equals(other.fileEntry)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FileEntryWorkNote [fileEntry=" + fileEntry + "]";
    }


}
