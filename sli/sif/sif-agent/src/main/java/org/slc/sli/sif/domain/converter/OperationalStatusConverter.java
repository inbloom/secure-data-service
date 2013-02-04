/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import openadk.library.student.OperationalStatus;

import org.springframework.stereotype.Component;

/**
 * Converter for SIF OperationalStatus to SLI operationStatus
 *
 * SLI values:
 * Active
 * Added
 * Changed Agency
 * Closed
 * Continuing
 * Future
 * Inactive
 * New
 * Reopened
 */
@Component
public class OperationalStatusConverter {

    private static final Map<OperationalStatus, String> OPERATIONAL_STATUS_MAP = new HashMap<OperationalStatus, String>();
    static {
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_CHANGED, "Changed Agency");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_CLOSED, "Closed");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_FUTURE, "Future");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_INACTIVE, "Inactive");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_NEW, "New");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.AGENCY_OPEN, "Active");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.CHANGED_BOUNDARY, null);
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.SCHOOL_CLOSED, "Closed");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.SCHOOL_FUTURE, "Future");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.SCHOOL_INACTIVE, "Inactive");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.SCHOOL_NEW, "New");
        OPERATIONAL_STATUS_MAP.put(OperationalStatus.SCHOOL_OPEN, "Active");
    }

    /**
     * Converts the SIF OperationalStatus into an SLI operationalStatus
     *
     * @param operationalStatus
     * @return
     */
    public String convert(OperationalStatus operationalStatus) {
        if (operationalStatus == null || operationalStatus.getValue() == null || operationalStatus.getValue().isEmpty()) {
            return null;
        }

        return OPERATIONAL_STATUS_MAP.get(operationalStatus);
    }
}
