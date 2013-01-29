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

/**
 * Unit of work, a structure that holds information about the work.
 *
 * @author okrook
 */
public class WorkNote implements Serializable {
    private static final long serialVersionUID = 5462350263804401592L;

    private final String batchJobId;
    private final String tenantId;
    private final boolean hasErrors;


    /**
     * Constructor for the class.
     *
     * @param batchJobId
     * @param tenantId
     */
    public WorkNote(String batchJobId, String tenantId, boolean hasErrors) {
        this.batchJobId = batchJobId;
        this.tenantId = tenantId;
        this.hasErrors = hasErrors;
    }

    /**
     * Gets the batch job id.
     *
     * @return String representing batch job id.
     */
    public String getBatchJobId() {
        return batchJobId;
    }

    /**
     * Gets the tenant id.
     *
     * @return String representing tenant id.
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WorkNote other = (WorkNote) obj;
        if (batchJobId == null) {
            if (other.batchJobId != null) {
                return false;
            }
        } else if (!batchJobId.equals(other.batchJobId)) {
            return false;
        }
        if (tenantId == null) {
            if (other.tenantId != null) {
                return false;
            }
        } else if (!tenantId.equals(other.tenantId)) {
            return false;
        }
        return true;
    }

    /**
     * @return the hasErrors
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SLIWorkNote [batchJobId=" + batchJobId + ", tenantId=" + tenantId + "]";
    }

}
