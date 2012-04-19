package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

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
        List<EntityBody> studentSectionGradebookEntries;
        if (sectionIds.size() != 0) {
            studentSectionGradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES,
                    ParameterConstants.SECTION_ID, sectionIds);
        } else {
            List<String> studentIds = new ArrayList<String>(optionalFieldAppenderHelper.getIdList(entities, "id"));
            studentSectionGradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES,
                    ParameterConstants.STUDENT_ID, studentIds);
        }

        //get all gradebook entry ids
        List<String> gradebookEntryIds = optionalFieldAppenderHelper.getIdList(studentSectionGradebookEntries, ParameterConstants.GRADEBOOK_ENTRY_ID);

        //get all gradebook entries for the sections
        List<EntityBody> gradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.GRADEBOOK_ENTRIES,
                "_id", gradebookEntryIds);

        for (EntityBody student : entities) {
            //get the student gradebook entries for the given student
            List<EntityBody> studentSectionGradebookEntriesForStudent = optionalFieldAppenderHelper.getEntitySubList(studentSectionGradebookEntries,
                    ParameterConstants.STUDENT_ID, (String) student.get("id"));

            for (EntityBody studentSectionGradebookEntry : studentSectionGradebookEntriesForStudent) {
                //get the gradebook entry for the student gradebook entry
                EntityBody gradebookEntry = optionalFieldAppenderHelper.getEntityFromList(gradebookEntries, "id",
                        (String) studentSectionGradebookEntry.get(ParameterConstants.GRADEBOOK_ENTRY_ID));

                if (gradebookEntry != null) {
                    studentSectionGradebookEntry.put(PathConstants.GRADEBOOK_ENTRIES, gradebookEntry);
                }
            }

            //add the body to the student
            student.put(PathConstants.STUDENT_SECTION_GRADEBOOK_ENTRIES, studentSectionGradebookEntriesForStudent);
        }

        return entities;
    }

}
