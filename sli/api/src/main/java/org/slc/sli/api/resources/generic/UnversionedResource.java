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

package org.slc.sli.api.resources.generic;

import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author jstokes
 */
@Component("unversionedResource")
@Scope("request")
public class UnversionedResource extends DefaultResource {

    public UnversionedResource() {
        super.setOnePartTemplate(ResourceTemplate.UNVERSIONED_ONE_PART);
        super.setTwoPartTemplate(ResourceTemplate.UNVERSIONED_TWO_PART);
    }
}
