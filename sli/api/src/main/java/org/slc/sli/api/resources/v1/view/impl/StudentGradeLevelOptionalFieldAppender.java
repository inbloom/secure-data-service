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


package org.slc.sli.api.resources.v1.view.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;

/**
 * Adds current grade level and current school data to the student
 *
 * @author dwu
 *
 */
@Component
public class StudentGradeLevelOptionalFieldAppender implements OptionalFieldAppender {

    private static final String ENTRY_GRADE_LEVEL = "entryGradeLevel";
    private static final String ENTRY_DATE = "entryDate";
    private static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";
    private static final String GRADE_LEVEL = "gradeLevel";
    private static final String SCHOOL_ID = "schoolId";

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {

        // get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");

        // Retrieve studentSchoolAssociations
        List<EntityBody> studentSchoolAssociationList = optionalFieldAppenderHelper.queryEntities(
                ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, ParameterConstants.STUDENT_ID, studentIds);

        if (studentSchoolAssociationList == null) {
            return entities;
        }

        // Variable initialization for date functions
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Loop through students
        for (EntityBody student : entities) {

            // Most recent grade level, not available till found
            String mostRecentGradeLevel = "Not Available";
            String mostRecentSchool = "";
            Date mostRecentEntry = null;

            // Try catch to stifle unexpected exceptions, and log them.
            // Returns "Not Available" for gradeLevel, when an exception is caught.
            try {

                // Loop through studentSchoolAssociations
                for (EntityBody studentSchoolAssociation : studentSchoolAssociationList) {

                    // If studentSchoolAssociation is not for this student, do nothing
                    if (!studentSchoolAssociation.get(ParameterConstants.STUDENT_ID).equals(student.get("id"))) {
                        continue;
                    }

                    // If student has an exitWithdrawDate earlier than today, continue searching for
                    // current grade
                    if (studentSchoolAssociation.containsKey(EXIT_WITHDRAW_DATE)) {
                        Date ssaDate = sdf.parse((String) studentSchoolAssociation.get(EXIT_WITHDRAW_DATE));
                        if (ssaDate.compareTo(currentDate) <= 0) {
                            continue;
                        }
                    }

                    // If student has no exitWithdrawDate, check for the latest entryDate
                    // Mark the entryGradeLevel with the most recent entryDate as the current grade
                    if (studentSchoolAssociation.containsKey(ENTRY_DATE)) {
                        Date ssaDate = sdf.parse((String) studentSchoolAssociation.get(ENTRY_DATE));

                        if (mostRecentEntry == null) {
                            mostRecentEntry = ssaDate;
                            mostRecentGradeLevel = (String) studentSchoolAssociation.get(ENTRY_GRADE_LEVEL);
                            mostRecentSchool = (String) studentSchoolAssociation.get(SCHOOL_ID);
                        } else {
                            if (ssaDate.compareTo(mostRecentEntry) > 0) {
                                mostRecentEntry = ssaDate;
                                mostRecentGradeLevel = (String) studentSchoolAssociation.get(ENTRY_GRADE_LEVEL);
                                mostRecentSchool = (String) studentSchoolAssociation.get(SCHOOL_ID);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                String exceptionMessage = "Exception while retrieving current gradeLevel for student with id:  "
                        + student.get("id") + " Exception: " + e.getMessage();
                debug(exceptionMessage);
                mostRecentGradeLevel = "Not Available";
            }

            student.put(GRADE_LEVEL, mostRecentGradeLevel);
            student.put(SCHOOL_ID, mostRecentSchool);
        }

        return entities;
    }
}
