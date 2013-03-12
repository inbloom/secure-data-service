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

package org.slc.sli.common.migration.config;

/**
 * A list of all supported strategies and the classes that implement those strategies.
 * 
 * @author pghosh
 * @author kmyers
 */
public enum Strategy {
    
    ADD("addStrategy"),
    REMOVE("removeFieldStrategy"),
    RENAME("renameFieldStrategy"),
    ADD_FIELD("addFieldStrategy"),
    ATTENDANCE("attendanceStrategy");

    private String implementationClassName;

    private Strategy(String implementingClassName) {
        this.implementationClassName = implementingClassName;
    }

    public String getBeanName() {
        return implementationClassName;
    }
}
