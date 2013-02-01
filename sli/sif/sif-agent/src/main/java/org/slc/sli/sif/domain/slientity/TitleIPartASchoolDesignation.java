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
package org.slc.sli.sif.domain.slientity;

/**
 * TitleIPartASchoolDesignation enumeration
 */
public enum TitleIPartASchoolDesignation {

    NOT_DESIGNATED("Not designated as a Title I Part A school"), PART_A_SCHOOLWIDE(
            "Title I Part A Schoolwide Assistance Program School"), PART_A_TARGETED(
            "Title I Part A Targeted Assistance School"), TARGETED_ELEGIBLE_NO_PROGRAM(
            "Title I targeted eligible school - no program"), TARGETED("Title I targeted school"), SCHOOLWIDE_ELIGIBLE_TARGETED(
            "Title I school wide eligible - Title I targeted program"), SCHOOLWIDE_ELIGIBLE_NO_PROGRAM(
            "Title I school wide eligible school - no program");

    private final String text;

    private TitleIPartASchoolDesignation(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
