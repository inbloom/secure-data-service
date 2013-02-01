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

package org.slc.sli.sif.domain.converter;

import java.util.HashMap;
import java.util.Map;

import openadk.library.common.YesNo;

import org.springframework.stereotype.Component;

/**
 * A custom converter to convert SIF YesNo to SLI boolean.
 *
 * @author vmcglaughlin
 *
 */
@Component
public class YesNoConverter {
    private static final Map<YesNo, Boolean> BOOLEAN_MAP = new HashMap<YesNo, Boolean>();
    static {
        BOOLEAN_MAP.put(YesNo.YES, Boolean.TRUE);
        BOOLEAN_MAP.put(YesNo.NO, Boolean.FALSE);
    }

    public Boolean convert(YesNo value) {
        return value == null ? null : BOOLEAN_MAP.get(value);
    }

}

