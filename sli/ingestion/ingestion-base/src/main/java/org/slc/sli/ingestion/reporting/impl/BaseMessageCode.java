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
 * Enumeration of message codes for the ingestion-base module.
 *
 * @author dduran
 *
 */
public enum BaseMessageCode implements MessageCode {

    BASE_0001,
    BASE_0002,
    BASE_0003,
    BASE_0004,
    BASE_0005,
    BASE_0006,
    BASE_0007,
    BASE_0009,
    BASE_0010,
    BASE_0013,
    BASE_0014,
    BASE_0015,
    BASE_0016,
    BASE_0017,
    BASE_0018,
    BASE_0020,
    BASE_0021,
    BASE_0022,
    BASE_0023,
    BASE_0024,
    BASE_0025,
    BASE_0026,
    BASE_0027;

    @Override
    public String getCode() {
        return this.name();
    }

}
