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

package org.slc.sli.sif.domain.slientity;

/**
 * Corresponding to the otherName defined in SLI schema.
 *
 * @author slee
 *
 */
public class OtherName extends Name {
    //otherName-only fields
    private String otherNameType;

    public OtherName() {
        super();
    }

    public String getOtherNameType() {
        return this.otherNameType;
    }

    public void setOtherNameType(String otherNameType) {
        this.otherNameType = otherNameType;
    }
}
