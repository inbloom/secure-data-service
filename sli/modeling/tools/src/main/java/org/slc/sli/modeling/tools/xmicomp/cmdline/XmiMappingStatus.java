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


package org.slc.sli.modeling.tools.xmicomp.cmdline;public enum XmiMappingStatus {    /**     * transient     */    TRANSIENT("transient"),    /**     * unknown     */    UNKNOWN("unknown"),    /**     * match     */    MATCH("match"),    /**     * ignorable     */    IGNORABLE("ignorable");    private final String name;        XmiMappingStatus(final String name) {        this.name = name;    }        public String getName() {        return name;    }        public static XmiMappingStatus valueOfName(final String name) {        for (final XmiMappingStatus value : values()) {            if (value.getName().equals(name)) {                return value;            }        }        return null;    }}