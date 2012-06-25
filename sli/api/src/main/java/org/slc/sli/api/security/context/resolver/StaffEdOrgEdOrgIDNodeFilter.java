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

package org.slc.sli.api.security.context.resolver;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Filters edorg/school id based on end date present in
 * StaffEgorgAssociation or teacherSchoolAssociation
 *
 * @author pghosh
 *
 */
@Component
public class StaffEdOrgEdOrgIDNodeFilter extends NodeDateFilter {

    private static final String END_DATE = "endDate";
    private static final String ZERO = "0";

    @PostConstruct
    public void setParameters() {
        setParameters(ZERO, END_DATE);
    }
}
