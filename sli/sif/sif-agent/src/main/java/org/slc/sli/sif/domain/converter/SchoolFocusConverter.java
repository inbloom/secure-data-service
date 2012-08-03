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

package org.slc.sli.sif.domain.converter;

import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;

/**
 * Converter for SchoolFocus to school type
 */
public class SchoolFocusConverter {

    /**
     * Converts the first school focus into a school type
     * @param schoolFocusList
     * @return
     */
    public String convert(SchoolFocusList schoolFocusList){

        if( schoolFocusList == null || schoolFocusList.getSchoolFocuses().length == 0 ){
            return null;
        }
        SchoolFocus[] schoolFocus = schoolFocusList.getSchoolFocuses();
        return toSliSchoolType(schoolFocus[0].getValue());
    }

    private String toSliSchoolType(String schoolFocusType) {
        if (SchoolFocusType.ALTERNATIVE.getValue().equals(schoolFocusType)) {
            return "Alternative";
        }
        if (SchoolFocusType.CHARTER.getValue().equals(schoolFocusType)) {
            return "JJAEP";
        }
        if (SchoolFocusType.MAGNET.getValue().equals(schoolFocusType)) {
            return "DAEP";
        }
        if (SchoolFocusType.REGULAR.getValue().equals(schoolFocusType)) {
            return "Regular";
        }
        if (SchoolFocusType.SPECIALED.getValue().equals(schoolFocusType)) {
            return "Special Education";
        }
        if (SchoolFocusType.VOCATIONAL.getValue().equals(schoolFocusType)) {
            return "Vocational";
        }
        return "Not Supported";
    }
}
