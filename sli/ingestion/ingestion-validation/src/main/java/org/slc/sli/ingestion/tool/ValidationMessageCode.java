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

package org.slc.sli.ingestion.tool;

import org.slc.sli.ingestion.reporting.MessageCode;

/**
 *Enumeration of message codes for the ingestion-validation module.
 * @author npandey
 *
 */
public enum ValidationMessageCode implements MessageCode {

    VALIDATION_0001,
    VALIDATION_0002,
    VALIDATION_0003,
    VALIDATION_0004,
    VALIDATION_0005,
    VALIDATION_0006,
    VALIDATION_0007,
    VALIDATION_0008,
    VALIDATION_0009,
    VALIDATION_0010,
    VALIDATION_0011,
    VALIDATION_0012,
    VALIDATION_0013,
    VALIDATION_0014,
    VALIDATION_0015,
    VALIDATION_0016;

    @Override
    public String getCode() {
        return this.name();
    }

}
