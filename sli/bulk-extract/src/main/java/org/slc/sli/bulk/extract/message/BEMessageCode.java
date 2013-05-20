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

package org.slc.sli.bulk.extract.message;

/**
 * Enumeration of message codes for the bulk extract module.
 *
 * @author npandey
 */
public enum BEMessageCode {

    BE_SE_CODE_0001,
    BE_SE_CODE_0002,
    BE_SE_CODE_0003,
    BE_SE_CODE_0004,
    BE_SE_CODE_0005,
    BE_SE_CODE_0006,
    BE_SE_CODE_0007,
    BE_SE_CODE_0008,
    BE_SE_CODE_0009,
    BE_SE_CODE_0010,
    BE_SE_CODE_0011,
    BE_SE_CODE_0012,
    BE_SE_CODE_0013,
    BE_SE_CODE_0014,
    BE_SE_CODE_0015,
    BE_SE_CODE_0016,
    BE_SE_CODE_0017;

    public String getCode() {
        return this.name();
    }

}
