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

package org.slc.sli.ingestion.reporting;

/**
 * Enumeration of message codes for the ingestion-core module.
 *
 * @author dduran
 *
 */
public enum CoreMessageCode implements MessageCode {
    CORE_0001,
    CORE_0002,
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
    CORE_0013,
    CORE_0014,
    CORE_0016,
    CORE_0017,
    CORE_0018,
    CORE_0019,
    CORE_0020,
    CORE_0021,
    CORE_0022,
    CORE_0023,
    CORE_0024,
    CORE_0025,
    CORE_0026,
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
    CORE_0037;

    @Override
    public String getCode() {
        return this.name();
    }

}
