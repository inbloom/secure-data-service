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

import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;

import org.springframework.stereotype.Component;

/**
 * Converter for SchoolFocus to school type
 */
@Component
public class SchoolFocusConverter {

    private static final Map<String, String> SCHOOL_FOCUS_MAP = new HashMap<String, String>();
    static {
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.ALTERNATIVE.getValue(), "Alternative");
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.CHARTER.getValue(), "JJAEP");
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.MAGNET.getValue(), "DAEP");
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.REGULAR.getValue(), "Regular");
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.SPECIALED.getValue(), "Special Education");
        SCHOOL_FOCUS_MAP.put(SchoolFocusType.VOCATIONAL.getValue(), "Vocational");
    }

    private static final String NOT_SUPPORTED = "Not Supported";

    /**
     * Converts the first school focus into a school type
     *
     * @param schoolFocusList
     * @return
     */
    public String convert(SchoolFocusList schoolFocusList) {

        if (schoolFocusList == null || schoolFocusList.getSchoolFocuses().length == 0) {
            return null;
        }
        SchoolFocus[] schoolFocus = schoolFocusList.getSchoolFocuses();
        String result = SCHOOL_FOCUS_MAP.get(schoolFocus[0].getValue());
        if (result != null) {
            return result;
        }
        return NOT_SUPPORTED;
    }

}
