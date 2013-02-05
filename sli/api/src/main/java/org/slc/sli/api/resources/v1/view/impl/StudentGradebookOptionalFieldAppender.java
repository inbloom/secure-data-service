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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;

/**
 *
 * Provides data about students and gradebook entries to construct the custom
 * views returned by the api
 * @author srupasinghe
 */
@Component
public class StudentGradebookOptionalFieldAppender implements OptionalFieldAppender {

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;


    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {

        //get the section Ids
        List<String> sectionIds = new ArrayList<String>(optionalFieldAppenderHelper.getSectionIds(entities));

        //get the list of student section grade book entries for all sections
        List<EntityBody> studentGradebookEntries;
        if (sectionIds.size() != 0) {
            studentGradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_GRADEBOOK_ENTRIES,
                    ParameterConstants.SECTION_ID, sectionIds);
        } else {
            List<String> studentIds = new ArrayList<String>(optionalFieldAppenderHelper.getIdList(entities, "id"));
            studentGradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_GRADEBOOK_ENTRIES,
                    ParameterConstants.STUDENT_ID, studentIds);
        }

        //get all gradebook entry ids
        List<String> gradebookEntryIds = optionalFieldAppenderHelper.getIdList(studentGradebookEntries, ParameterConstants.GRADEBOOK_ENTRY_ID);

        //get all gradebook entries for the sections
        List<EntityBody> gradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.GRADEBOOK_ENTRIES,
                "_id", gradebookEntryIds);

        for (EntityBody student : entities) {
            //get the student gradebook entries for the given student
            List<EntityBody> studentGradebookEntriesForStudent = optionalFieldAppenderHelper.getEntitySubList(studentGradebookEntries,
                    ParameterConstants.STUDENT_ID, (String) student.get("id"));

            for (EntityBody studentGradebookEntry : studentGradebookEntriesForStudent) {
                //get the gradebook entry for the student gradebook entry
                EntityBody gradebookEntry = optionalFieldAppenderHelper.getEntityFromList(gradebookEntries, "id",
                        (String) studentGradebookEntry.get(ParameterConstants.GRADEBOOK_ENTRY_ID));

                if (gradebookEntry != null) {
                    studentGradebookEntry.put(PathConstants.GRADEBOOK_ENTRIES, gradebookEntry);
                }
            }

            //add the body to the student
            student.put(PathConstants.STUDENT_GRADEBOOK_ENTRIES, studentGradebookEntriesForStudent);
        }

        return entities;
    }

}
