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

package org.slc.sli.sif.domain.converter;

import openadk.library.student.OperationalStatus;

/**
 * Converter for SIF OperationalStatus to SLI operationStatus
 */
public class OperationalStatusConverter {

    /**
     * Converts the SIF OperationalStatus into an SLI operationalStatus
     * @param operationalStatus
     * @return
     */
    public String convert(OperationalStatus operationalStatus){
        if (operationalStatus == null || operationalStatus.getValue().isEmpty()) {
            return null;
        }

        return toSliOperationalStatus(operationalStatus.getValue());
    }

    private String toSliOperationalStatus(String operationalStatus) {
        if (OperationalStatus.SCHOOL_CLOSED.getValue().equals(operationalStatus)) {
            return "Closed";
        }
        if (OperationalStatus.SCHOOL_FUTURE.getValue().equals(operationalStatus)) {
            return "Future";
        }
        if (OperationalStatus.SCHOOL_INACTIVE.getValue().equals(operationalStatus)) {
            return "Inactive";
        }
        if (OperationalStatus.SCHOOL_NEW.getValue().equals(operationalStatus)) {
            return "New";
        }
        if (OperationalStatus.SCHOOL_OPEN.getValue().equals(operationalStatus)) {
            return "Reopened";
        }
        if (OperationalStatus.CHANGED_BOUNDARY.getValue().equals(operationalStatus)) {
            return "Changed Agency";
        }

        return "Not Supported";
    }
}
