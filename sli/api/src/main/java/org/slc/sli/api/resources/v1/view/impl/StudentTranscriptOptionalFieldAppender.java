package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;

/**
 * Provides data about students and transcript history to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentTranscriptOptionalFieldAppender implements OptionalFieldAppender {

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {

        //get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");

        //get the student section associations for the students
        List<EntityBody> studentSectionAssociations = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_SECTION_ASSOCIATIONS,
                ParameterConstants.STUDENT_ID, studentIds);

        //get sections for the student
        List<String> sectionIds = optionalFieldAppenderHelper.getIdList(studentSectionAssociations, ParameterConstants.SECTION_ID);
        List<EntityBody> sections = optionalFieldAppenderHelper.queryEntities(ResourceNames.SECTIONS, "_id", sectionIds);

        //get the studentAcademicRecords
        List<EntityBody> studentAcademicRecords = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_ACADEMIC_RECORDS,
                ParameterConstants.STUDENT_ID, studentIds);
        List<String> studentAcademicRecordIds = optionalFieldAppenderHelper.getIdList(studentAcademicRecords, "id");

        //get the transcripts
        List<EntityBody> studentTranscripts = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS,
               ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, studentAcademicRecordIds);

        //get the sessions
        List<String> sessionIds = optionalFieldAppenderHelper.getIdList(sections, ParameterConstants.SESSION_ID);
        List<EntityBody> sessions = optionalFieldAppenderHelper.queryEntities(ResourceNames.SESSIONS, "_id", sessionIds);

        //get the courses
        List<String> courseIds = optionalFieldAppenderHelper.getIdList(sections, ParameterConstants.COURSE_ID);
        List<EntityBody> courses = optionalFieldAppenderHelper.queryEntities(ResourceNames.COURSES,
                "_id", courseIds);

        //get the student transcripts for the students
        List<EntityBody> studentAcademicRecordsForStudents = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_ACADEMIC_RECORDS,
                ParameterConstants.STUDENT_ID, studentIds);

        for (EntityBody student : entities) {
            String studentId = (String) student.get("id");

            List<EntityBody> studentSectionAssociationsForStudent = optionalFieldAppenderHelper.getEntitySubList(studentSectionAssociations,
                    ParameterConstants.STUDENT_ID, studentId);

            List<EntityBody> studentTranscriptAssociations = new ArrayList<EntityBody>();
            for (EntityBody studentSectionAssociationForStudent : studentSectionAssociationsForStudent) {
                String sectionId = (String) studentSectionAssociationForStudent.get(ParameterConstants.SECTION_ID);

                //get the section for the student
                EntityBody sectionForStudent = optionalFieldAppenderHelper.getEntityFromList(sections, "id", sectionId);

                if (sectionForStudent == null)
                    continue;
                
                //get the session for the section
                EntityBody sessionForSection = optionalFieldAppenderHelper.getEntityFromList(sessions, "id",
                        (String) sectionForStudent.get(ParameterConstants.SESSION_ID));

                EntityBody courseForSection = optionalFieldAppenderHelper.getEntityFromList(courses, "id",
                        (String) sectionForStudent.get(ParameterConstants.COURSE_ID));

                //get the student transcripts for the student
                List<EntityBody> studentAcademicRecordsForStudent = optionalFieldAppenderHelper.getEntitySubList(studentAcademicRecordsForStudents,
                        ParameterConstants.STUDENT_ID, studentId);

                List<String> studentAcademicRecordIdsForStudent = optionalFieldAppenderHelper.getIdList(studentAcademicRecordsForStudent, "id");

                List<EntityBody> studentTranscriptsForStudent = new ArrayList<EntityBody>();

                for (String studentAcademicRecordId : studentAcademicRecordIdsForStudent) {
                    studentTranscriptsForStudent.addAll(optionalFieldAppenderHelper.getEntitySubList(studentTranscripts,
                            ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, studentAcademicRecordId));
                }

                //get the student transcripts for the student and the course
                List<EntityBody> studentTranscriptsForStudentAndCourse = optionalFieldAppenderHelper.getEntitySubList(studentTranscriptsForStudent,
                        ParameterConstants.COURSE_ID, (String) sectionForStudent.get(ParameterConstants.COURSE_ID));
                studentTranscriptAssociations.addAll(studentTranscriptsForStudentAndCourse);

                //add the session and course into the section
                sectionForStudent.put(PathConstants.SESSIONS, sessionForSection);
                sectionForStudent.put(PathConstants.COURSES, courseForSection);

                //add the expanded items to the association
                studentSectionAssociationForStudent.put(PathConstants.SECTIONS, sectionForStudent);
                //studentSectionAssociationForStudent.put(PathConstants.COURSE_TRANSCRIPTS, studentTranscriptsForStudentAndCourse);
            }

            EntityBody body = new EntityBody();
            body.put(PathConstants.STUDENT_SECTION_ASSOCIATIONS, studentSectionAssociationsForStudent);
            body.put(PathConstants.COURSE_TRANSCRIPTS, studentTranscriptAssociations);

            //add the associations to the student
            student.put(ParameterConstants.OPTIONAL_FIELD_TRANSCRIPT, body);
        }

        return entities;
    }



}
