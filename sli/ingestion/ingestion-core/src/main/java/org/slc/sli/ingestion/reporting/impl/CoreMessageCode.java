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

package org.slc.sli.ingestion.reporting.impl;

import org.slc.sli.ingestion.reporting.MessageCode;

/**
 * Enumeration of message codes for the ingestion-core module.
 *
 * @author dduran
 *
 */
public enum CoreMessageCode implements MessageCode {
    CORE_0001,
    CORE_0003,
    CORE_0004,
    CORE_0005,
    CORE_0006,
    CORE_0007,
    CORE_0008,
    CORE_0009,
    CORE_0010,
    CORE_0011,
    CORE_0012,
    CORE_0014,
    CORE_0015,
    CORE_0018,
    CORE_0027,
    CORE_0028,
    CORE_0029,
    CORE_0030,
    CORE_0031,
    CORE_0032,
    CORE_0033,
    CORE_0034,
    CORE_0035,
    CORE_0036,
    CORE_0037,
    CORE_0038,
    CORE_0039,
    CORE_0040,
    CORE_0041,
    CORE_0042,
    CORE_0043,
    CORE_0044,
    CORE_0045,
    CORE_0046,
    CORE_0047,
    CORE_0048,
    CORE_0049,
    CORE_0050,
    CORE_0051,
    CORE_0052,
    CORE_0056,
    CORE_0057,
    CORE_0058,
    CORE_0059,
    CORE_0060,
    CORE_0061,
    CORE_0062,
    CORE_0063;

    @Override
    public String getCode() {
        return this.name();
    }

}
