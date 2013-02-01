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

package org.slc.sli.api.resources.generic.util;

/**
 * @author jstokes
 */
public enum ResourceTemplate {
    ONE_PART("/rest/{version}/{resource}"),
    TWO_PART("/rest/{version}/{resource}/{id}"),
    THREE_PART("/rest/{version}/{base}/{id}/{resource}"),
    FOUR_PART("/rest/{version}/{base}/{id}/{association}/{resource}"),
    FIVE_PART("/rest/{version}/{base}/{id}/{association}/{resource}/{part5}"),
    SIX_PART("/rest/{version}/{base}/{id}/{association}/{resource}/{part5}/{part6}"),
    CUSTOM("/rest/{version}/{resource}/{id}/custom"),
    AGGREGATES("/rest/{version}/{resource}/{id}/aggregations"),
    CALCULATED_VALUES("/rest/{version}/{resource}/{id}/calculatedValues"),
    SEARCH("/rest/{version}/{resource}/{entity}"),
    UNVERSIONED_ONE_PART("/rest/{resource}"),
    UNVERSIONED_TWO_PART("/rest/{resource}/{id}");

    private final String template;

    private ResourceTemplate(final String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
