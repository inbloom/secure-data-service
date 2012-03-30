package org.slc.sli.api.resources.v1.view.impl;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {

        //get the section Ids
        List<String> sectionIds = getSectionIds(entities);

        //get the list of student section grade book entries for all sections
        List<EntityBody> studentSectionGradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES,
                ParameterConstants.SECTION_ID, sectionIds);

        //get all gradebook entry ids
        List<String> gradebookEntryIds = optionalFieldAppenderHelper.getIdList(studentSectionGradebookEntries, ParameterConstants.GRADEBOOK_ENTRY_ID);

        //get all gradebook entries for the sections
        List<EntityBody> gradebookEntries = optionalFieldAppenderHelper.queryEntities(ResourceNames.GRADEBOOK_ENTRIES,
                "_id", gradebookEntryIds);

        for (EntityBody student : entities) {
            //get the student gradebook entries for the given student
            List<EntityBody> studentSectionGradebookEntriesForStudent = optionalFieldAppenderHelper.getEntitySubList(studentSectionGradebookEntries,
                    ParameterConstants.STUDENT_ID, (String) student.get("id"));

            List<EntityBody> gradebookEntriesForStudent = new ArrayList<EntityBody>();
            for (EntityBody studentSectionGradebookEntry : studentSectionGradebookEntriesForStudent) {
                //get the gradebook entry for the student gradebook entry
                EntityBody gradebookEntry = optionalFieldAppenderHelper.getEntityFromList(gradebookEntries, "id",
                        (String) studentSectionGradebookEntry.get(ParameterConstants.GRADEBOOK_ENTRY_ID));

                if (gradebookEntry != null) {
                    gradebookEntriesForStudent.add(gradebookEntry);
                }
            }

            //create the map to hold the studentSectionGradebookEntries and gradebookentries
            EntityBody body = new EntityBody();
            body.put(PathConstants.STUDENT_SECTION_GRADEBOOK_ENTRIES, studentSectionGradebookEntriesForStudent);
            body.put(PathConstants.GRADEBOOK_ENTRIES, gradebookEntriesForStudent);

            //add the body to the student
            student.put(ParameterConstants.OPTIONAL_FIELD_GRADEBOOK, body);
        }

        return entities;
    }

    /**
     * Returns a list of section ids by examining a list of students
     * @param entities
     * @return
     */
    protected List<String> getSectionIds(List<EntityBody> entities) {
        List<String> sectionIds = new ArrayList<String>();
        for (EntityBody e : entities) {
            List<EntityBody> associations = (List<EntityBody>) e.get("studentSectionAssociation");

            for (EntityBody association : associations) {
                sectionIds.add((String) association.get(ParameterConstants.SECTION_ID));
            }
        }

        return sectionIds;
    }
}
