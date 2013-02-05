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

import org.springframework.stereotype.Component;

import openadk.library.common.EnglishProficiency;
import openadk.library.common.EnglishProficiencyCode;

/**
 * A custom converter to convert SIF EnglishProficiency and ELL to SLI limitedEnglishProficiency.
 *
 * @author syau
 *
 */
@Component
public class EnglishProficiencyConverter {

    private static final Map<EnglishProficiencyCode, String> ENGLISH_PROFICIENCY_MAP = new HashMap<EnglishProficiencyCode, String>();
    static {
        ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.NATIVE_ENGLISH, "NotLimited");
        ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.FLUENT_ENGLISH, "NotLimited");
        ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.NON_ENGLISH_SPEAKING, "Limited");
        ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.REDESIGNATED_AS_FLUENT, "NotLimited");
        ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.LIMITED_ENGLISH, "Limited");
        // ENGLISH_PROFICIENCY_MAP.put(EnglishProficiencyCode.STATUS_UNKNOWN, "");
    }

    public String convert(EnglishProficiency ep) {
        if (ep == null) {
            return null;
        }
        
        EnglishProficiencyCode code = EnglishProficiencyCode.wrap(ep.getCode());
        return ENGLISH_PROFICIENCY_MAP.get(code);
    }

}
