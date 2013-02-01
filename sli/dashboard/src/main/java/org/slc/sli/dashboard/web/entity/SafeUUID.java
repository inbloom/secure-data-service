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

package org.slc.sli.dashboard.web.entity;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validatable UUID string to be accepted as a modelattribute, requestparam, or pathvariable
 * 
 * @author agrebneva
 * 
 */
public class SafeUUID {
    @Size(max = 43, message = "Not a valid UUID")
    @Pattern(regexp = "[A-Za-z0-9-]{50}(?:_id)?")
    String uuid;
    
    public SafeUUID() {
        //Default constructor
    }
    
    public SafeUUID(String id) {
        this.uuid = id;
    }
    
    public String getId() {
        return uuid;
    }
    
    public void setId(String uuid) {
        this.uuid = uuid;
    }
}
