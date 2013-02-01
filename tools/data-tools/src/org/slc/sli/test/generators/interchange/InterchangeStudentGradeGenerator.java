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

package org.slc.sli.test.generators.interchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.stream.XMLStreamException;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptor;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptorType;
import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.Diploma;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.InterchangeStudentGrade;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.SLCCourseIdentityType;
import org.slc.sli.test.edfi.entities.SLCCourseReferenceType;
import org.slc.sli.test.edfi.entities.SLCCourseTranscript;
import org.slc.sli.test.edfi.entities.SLCDiplomaReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCGrade;
import org.slc.sli.test.edfi.entities.SLCGradeIdentityType;
import org.slc.sli.test.edfi.entities.SLCGradeReferenceType;
import org.slc.sli.test.edfi.entities.SLCGradebookEntry;
import org.slc.sli.test.edfi.entities.SLCGradebookEntryIdentityType;
import org.slc.sli.test.edfi.entities.SLCGradebookEntryReferenceType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.SLCLearningObjective;
import org.slc.sli.test.edfi.entities.SLCLearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.SLCLearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.SLCReportCard;
import org.slc.sli.test.edfi.entities.SLCReportCardIdentityType;
import org.slc.sli.test.edfi.entities.SLCReportCardReferenceType;
import org.slc.sli.test.edfi.entities.SLCSectionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;
import org.slc.sli.test.edfi.entities.SLCSessionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSessionReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentAcademicRecord;
import org.slc.sli.test.edfi.entities.SLCStudentAcademicRecordIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentAcademicRecordReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentCompetency;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentGradebookEntry;
import org.slc.sli.test.edfi.entities.SLCStudentIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentSectionAssociationIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentSectionAssociationReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.GradeBookEntryMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.LearningObjectiveMeta;
import org.slc.sli.test.edfi.entities.meta.ReportCardMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.meta.relations.StudentGradeRelations;
import org.slc.sli.test.generators.CalendarDateGenerator;
import org.slc.sli.test.generators.SessionGenerator;
import org.slc.sli.test.generators.StudentCompetancyObjectiveGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentGradeGenerator;
import org.slc.sli.test.utils.InterchangeWriter;

/**
 * Generates the StudentGradeGrade Interchange
 *
 * @author ldalgado
 *
 */
public final class InterchangeStudentGradeGenerator {

    private static final String ID_PREFIX_STUDENT_ACADEMIC_RECORD = "StudentAcademicRecord_";
    private static final String ID_PREFIX_COURSE_TRANSCRIPT = "CourseTranscript_";
    private static final String ID_PREFIX_REPORT_CARD = "ReportCard_";
    private static final String ID_PREFIX_GRADE = "Grade_";
    private static final String ID_PREFIX_LO = "LearningObjective_";
    private static final String ID_PREFIX_SCO = "SCO_";
    private static final String ID_PREFIX_CLD = "CLD_";

    private static Map<String, List<SLCGrade>> gradeMap = new HashMap<String, List<SLCGrade>>();

    // LightWeight object that holds id of the reference.
    static final class Ref extends ComplexObjectType {

        public Ref(String id) {

            this.id = id;
        }
    }

    private static SLCGradingPeriodReferenceType getGradingPeriodRef(String schoolId, GradingPeriodMeta gpMeta) {
        SLCGradingPeriodReferenceType gradingPeriodRef = new SLCGradingPeriodReferenceType();
        SLCGradingPeriodIdentityType gradingPeriodIdentity = new SLCGradingPeriodIdentityType();
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        // Calendar cal = new GregorianCalendar(Integer.parseInt( gpMeta.getBeginData() ), 1, 1);

        // TODO use a real date associated with gradingPeriods
        eoit.setStateOrganizationId(schoolId);
        eort.setEducationalOrgIdentity(eoit);
        gradingPeriodIdentity.setEducationalOrgReference(eort);
        gradingPeriodIdentity.setGradingPeriod(GradingPeriodType.FIRST_NINE_WEEKS);
        gradingPeriodIdentity.setBeginDate(CalendarDateGenerator.generatDate());
        gradingPeriodRef.setGradingPeriodIdentity(gradingPeriodIdentity);
        return gradingPeriodRef;
    }

    private static SLCEducationalOrgReferenceType getEducationalOrgRef(String schoolId) {

        SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();// References
                                                                                       // to
        // EducationalOrg
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();

        // Old style references
        // EducationOrgIdentificationCode edOrgCode = new EducationOrgIdentificationCode();
        // edOrgCode.setID(schoolId);
        // edOrgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL);

        edOrgIdentity.setStateOrganizationId(schoolId);
        edOrgRef.setEducationalOrgIdentity(edOrgIdentity);

        // Old style references
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgCode);
        // edOrgIdentity.getEducationOrgIdentificationCode().add(edOrgCode);

        return edOrgRef;
    }

    private static SLCSectionReferenceType getSectionRef(String sectionId, String schoolId) {

        SLCSectionReferenceType sectionRef = new SLCSectionReferenceType();
        SLCSectionIdentityType sectionIdentity = new SLCSectionIdentityType();
        sectionIdentity.setUniqueSectionCode(sectionId);
        sectionRef.setSectionIdentity(sectionIdentity);

        SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();

        edOrgIdentity.setStateOrganizationId(schoolId);
        edOrgRef.setEducationalOrgIdentity(edOrgIdentity);

        sectionIdentity.setEducationalOrgReference(edOrgRef);
        return sectionRef;
    }

    public static void generate(InterchangeWriter<InterchangeStudentGrade> writer) throws XMLStreamException {

        StudentGradeRelations.buildCompetencyLevelDescriptorMeta();
        StudentGradeRelations.buildGradeBookEntriesMeta();
        StudentGradeRelations.buildReportCardMeta();
        InterchangeStudentGrade interchange = new InterchangeStudentGrade();
        List<ComplexObjectType> interchangeObjects = interchange
                .getStudentAcademicRecordOrCourseTranscriptOrReportCard();

        writeEntitiesToInterchange(interchangeObjects, writer);
    }

    private static void writeEntitiesToInterchange(List<ComplexObjectType> interchangeObjects,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        int studentCount = MetaRelations.STUDENT_MAP.size();
        long count = 0;

        generateStudentAcademicRecord(MetaRelations.STUDENT_MAP, MetaRelations.SESSION_MAP, MetaRelations.SECTION_MAP,
                StudentGradeRelations.REPORT_CARD_META, writer);
        System.out.println("Finished StudentAcademicRecord [" + studentCount + "] Records Generated");
        generateCourseTranscript(MetaRelations.STUDENT_MAP, MetaRelations.SESSION_MAP, MetaRelations.COURSE_MAP,
                MetaRelations.COURSES_PER_STUDENT, writer);
        System.out.println("Finished CourseTranscript [" + studentCount * MetaRelations.COURSES_PER_STUDENT
                + "] Records Generated");
        generateGrade(MetaRelations.STUDENT_MAP, StudentGradeRelations.REPORT_CARD_META, MetaRelations.SECTION_MAP,
                writer);
        System.out.println("Finished Grade [" + studentCount * MetaRelations.COURSES_PER_STUDENT
                * MetaRelations.SECTIONS_PER_COURSE_SESSION + "] Records Generated");
        generateReportCard(MetaRelations.STUDENT_MAP, StudentGradeRelations.REPORT_CARD_META,
                MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished ReportCard [" + studentCount * StudentGradeRelations.REPORT_CARDS
                + "] Records Generated");
        generateStudentCompetency(MetaRelations.STUDENT_MAP, StudentGradeRelations.REPORT_CARD_META,
                MetaRelations.SECTION_MAP, writer);
        System.out
                .println("Finished StudentCompetency ["
                        + studentCount
                        * StudentGradeRelations.REPORT_CARDS
                        * (StudentGradeRelations.LEARNING_OBJECTIVES_PER_REPORT + StudentGradeRelations.STUDENT_COMPETENCY_OBJECTIVE_PER_REPORT)
                        + "] Records Generated");
        generateDiploma(writer);
        System.out.println("Finished Diploma [O] Records Generated ");
        generateGradebookEntry(StudentGradeRelations.GRADE_BOOK_ENTRY_METAS, MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished GradebookEntry [" + StudentGradeRelations.GRADEBOOK_ENTRIES
                + "] Records Generated");
        count = generateStudentGradebookEntry(MetaRelations.STUDENT_MAP, StudentGradeRelations.GRADE_BOOK_ENTRY_METAS,
                MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished StudentGradebookEntry [" + count + "] Records Generated");
        generateCompentencyLevelDescriptor(StudentGradeRelations.competencyLevelDescriptors, writer);
        System.out.println("Finished CompentencyLevelDescriptor [" + StudentGradeRelations.COMPETENCY_LEVEL_DESCRIPTOR
                + "] Records Generated");
        generateLearningObjective(StudentGradeRelations.REPORT_CARD_META, writer);
        System.out.println("Finished LearningObjective [" + StudentGradeRelations.learningObjectives.size()
                + "] Records Generated");
        generateStudentCompentencyObjective(StudentGradeRelations.REPORT_CARD_META, writer);
        System.out.println("Finished StudentCompentencyObjective ["
                + StudentGradeRelations.studentCompetencyObjectives.size() + "] Records Generated");

    }

    private static void generateStudentAcademicRecord(Map<String, StudentMeta> studentMetaMap,
            Map<String, SessionMeta> sessionMetaMap, Map<String, SectionMeta> sectionMetaMap,
            List<ReportCardMeta> reportCardsForStudent, InterchangeWriter<InterchangeStudentGrade> writer) {

        String sessionId = sessionMetaMap.keySet().iterator().next();
        SessionMeta sessionMeta = sessionMetaMap.get(sessionId);
        // Reference to First Session.
        // All Academic Records are for
        // the first Session in
        // SessionMetaMap
        SLCSessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(sessionMeta.schoolId, sessionId);

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            int gradingPeriodIndex = 0;
            String studentId = studentMeta.id;
            SLCStudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);// Reference
                                                                                                     // to
                                                                                                     // Student

            List<SLCReportCardReferenceType> reportCardRefs = new ArrayList<SLCReportCardReferenceType>();
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                SLCReportCardReferenceType reportCardRef = new SLCReportCardReferenceType();
                SLCReportCardIdentityType reportCardId = new SLCReportCardIdentityType();

                // Create our bogus grading period reference
                SLCGradingPeriodReferenceType gradingPeriodRef = new SLCGradingPeriodReferenceType();
                SLCGradingPeriodIdentityType gradingPeriodId = new SLCGradingPeriodIdentityType();

                // TODO pull this out into an SLCGradingPeriodIdentityType
                gradingPeriodId.setGradingPeriod(GradingPeriodType.values()[gradingPeriodIndex++]);

                // KLUDGE TODO make this date match up with an actual grading period - perhaps even
                // use reportCardMeta :(
                gradingPeriodId.setBeginDate(CalendarDateGenerator.generatDate());

                SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
                SLCEducationalOrgIdentityType edOrgId = new SLCEducationalOrgIdentityType();
                edOrgId.setStateOrganizationId(sessionMeta.schoolId);
                edOrgRef.setEducationalOrgIdentity(edOrgId);
                gradingPeriodId.setEducationalOrgReference(edOrgRef);

                gradingPeriodRef.setGradingPeriodIdentity(gradingPeriodId);

                reportCardId.setGradingPeriodReference(gradingPeriodRef);
                reportCardId.setStudentReference(studentRef);

                reportCardRef.setReportCardIdentity(reportCardId);
                reportCardRefs.add(reportCardRef);

                // original
                // reportCardRef.setRef(new Ref(studentId + "_" + ID_PREFIX_REPORT_CARD +
                // reportCardMeta.getId()));
                // reportCardRefs.add(reportCardRef);// References to ReportCards
            }

            SLCDiplomaReferenceType diplomaRef = null;// Reference to Diploma. Not used
            SLCStudentAcademicRecord sar = StudentGradeGenerator.getStudentAcademicRecord(studentRef, sessionRef,
                    reportCardRefs, diplomaRef);
            sar.getReportCardReference();

            sar.setId(ID_PREFIX_STUDENT_ACADEMIC_RECORD + studentId);

            writer.marshal(sar);
        }
    }

    private static void generateCourseTranscript(Map<String, StudentMeta> studentMetaMap,
            Map<String, SessionMeta> sessionMetaMap, Map<String, CourseMeta> courseMetaMap, int coursesPerStudent,
            InterchangeWriter<InterchangeStudentGrade> writer) {
        Random rand = new Random(31);

        String[] sessionIds = sessionMetaMap.keySet().toArray(new String[0]);

        List<String> courseSet = new LinkedList<String>(courseMetaMap.keySet());
        courseSet = courseSet.subList(0, coursesPerStudent);// Every Student has a CourseTranscript
                                                            // for the first N Courses in
                                                            // CourseMetaMap

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            SLCStudentAcademicRecordReferenceType sarRef = new SLCStudentAcademicRecordReferenceType();
            SLCStudentAcademicRecordIdentityType sarId = new SLCStudentAcademicRecordIdentityType();
            SLCStudentReferenceType studentRef = new SLCStudentReferenceType();
            SLCStudentIdentityType studentIdType = new SLCStudentIdentityType();
            studentIdType.setStudentUniqueStateId(studentId);
            studentRef.setStudentIdentity(studentIdType);
            sarId.setStudentReference(studentRef);
            SLCSessionReferenceType sessionRef = new SLCSessionReferenceType();
            SLCSessionIdentityType sessionId = new SLCSessionIdentityType();
            sessionId.setSessionName(sessionIds[rand.nextInt(sessionIds.length)]);
            sessionRef.setSessionIdentity(sessionId);
            sarId.setSessionReference(sessionRef);
            sarRef.setStudentAcademicRecordIdentity(sarId);

            for (String cId : courseSet) {
                CourseMeta courseMeta = courseMetaMap.get(cId);
                String courseId = courseMeta.id;
                String courseSchool = courseMeta.schoolId;

                SLCCourseReferenceType courseRef = new SLCCourseReferenceType();// References to
                                                                                // Course
                SLCCourseIdentityType courseIdentity = new SLCCourseIdentityType();
                SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
                eoit.setStateOrganizationId(courseMeta.schoolId);
                SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
                eort.setEducationalOrgIdentity(eoit);
                courseIdentity.setEducationalOrgReference(eort);
                courseIdentity.setUniqueCourseId(courseMeta.uniqueCourseId);
                courseRef.setCourseIdentity(courseIdentity);

                // IDREF deprecated
                // sarRef.setRef(new Ref(ID_PREFIX_STUDENT_ACADEMIC_RECORD + studentId));

                SLCEducationalOrgReferenceType edOrgRef = getEducationalOrgRef(courseSchool);// Reference
                                                                                             // to
                                                                                             // EducationalOrg

                SLCCourseTranscript courseTranscript = StudentGradeGenerator.getCourseTranscript(courseRef, sarRef,
                        edOrgRef);
                courseTranscript.setId(ID_PREFIX_COURSE_TRANSCRIPT + courseId + "_" + studentId);
                writer.marshal(courseTranscript);
            }
        }
    }

    private static void generateReportCard(Map<String, StudentMeta> studentMetaMap,
            List<ReportCardMeta> reportCardsForStudent, Map<String, SectionMeta> sectionMetaMap,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;
            String schoolId = studentMeta.schoolIds.get(0);

            SLCStudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                String reportCardId = reportCardMeta.getId();
                List<SLCGradeReferenceType> gradeReferences = new ArrayList<SLCGradeReferenceType>();// References
                // to Grades.
                // One Grade
                // per
                // Section.N
                // Sections per
                // Course

                // One Grade per Section. N Sections per Course. N Courses per Student. We will use
                // the first N Sections
                List<SLCGrade> gradeList = gradeMap.get(studentId);

                for (SLCGrade grade : gradeList) {
                    SLCGradeIdentityType git = new SLCGradeIdentityType();
                    git.setGradingPeriodReference(grade.getGradingPeriodReference());
                    SLCStudentSectionAssociationReferenceType slcssa = new SLCStudentSectionAssociationReferenceType();
                    SLCStudentSectionAssociationReferenceType ssa = grade.getStudentSectionAssociationReference();

                    slcssa.setStudentSectionAssociationIdentity(ssa.getStudentSectionAssociationIdentity());
                    SLCStudentSectionAssociationIdentityType slcssai = new SLCStudentSectionAssociationIdentityType();
                    SLCStudentSectionAssociationIdentityType ssai = ssa.getStudentSectionAssociationIdentity();

                    slcssai.setSectionReference(ssai.getSectionReference());
                    slcssai.setStudentReference(ssai.getStudentReference());
                    slcssai.setBeginDate(CalendarDateGenerator.generatDate());

                    slcssa.setStudentSectionAssociationIdentity(slcssai);

                    git.setStudentSectionAssociationReference(slcssa);
                    SLCGradeReferenceType grt = new SLCGradeReferenceType();
                    grt.setGradeIdentity(git);
                    gradeReferences.add(grt);
                }

                GradingPeriodMeta gpMeta = reportCardMeta.getGradingPeriod();// Reference to
                                                                             // GradingPeriod
                SLCGradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(schoolId, gpMeta);

                SLCReportCard reportCard = StudentGradeGenerator.getReportCard(studentRef, gradingPeriodRef,
                        gradeReferences, null);
                reportCard.setId(studentId + "_" + ID_PREFIX_REPORT_CARD + reportCardMeta.getId());

                writer.marshal(reportCard);
            }
        }
    }

    private static void generateGrade(Map<String, StudentMeta> studentMetaMap,
            List<ReportCardMeta> reportCardsForStudent, Map<String, SectionMeta> sectionMetaMap,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            SLCStudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {

                String reportCardId = reportCardMeta.getId();

                for (String sectionId : studentMeta.sectionIds) {// One Grade per Section. N
                                                                 // Sections per
                    // Course. N Courses per Student. We will use
                    // the first N Sections
                    String schoolId = sectionMetaMap.get(sectionId).schoolId;

                    SLCSectionReferenceType sectionRef = getSectionRef(sectionId, schoolId);// Reference
                                                                                            // to
                                                                                            // Section

                    GradingPeriodMeta gpMeta = reportCardMeta.getGradingPeriod();
                    SLCGradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(schoolId, gpMeta);

                    SLCStudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);// Reference
                                                                                           // to
                                                                                           // StudentSectionAssociation
                    SLCGrade grade = StudentGradeGenerator.getGrade(ssaRef, gradingPeriodRef);
                    grade.setId(ID_PREFIX_GRADE + reportCardId + sectionId + studentId);
                    if (!gradeMap.containsKey(studentId)) {
                        List<SLCGrade> gradeList = new ArrayList<SLCGrade>();
                        gradeList.add(grade);
                        gradeMap.put(studentId, gradeList);
                    } else {
                        gradeMap.get(studentId).add(grade);
                    }

                    writer.marshal(grade);
                }
            }
        }
    }

    private static void generateStudentCompetency(Map<String, StudentMeta> studentMetaMap,
            List<ReportCardMeta> reportCardsForStudent, Map<String, SectionMeta> sectionMetaMap,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        int learningObjectiveIdCounter = 0;

        LearningObjectiveMeta lom = LearningObjectiveMeta.create(ID_PREFIX_LO + learningObjectiveIdCounter);
        AssessmentMetaRelations.LEARNING_OBJECTIVE_MAP.put(ID_PREFIX_LO + learningObjectiveIdCounter, lom);
        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            SLCStudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                String reportCardId = reportCardMeta.getId();

                List<String> loIds = reportCardMeta.getLearningObjectiveIds();
                List<SectionMeta> loSections = reportCardMeta.getLearningObjectiveSections();
                for (int i = 0; i < loIds.size(); i++) {

                    String loId = loIds.get(i);
                    SectionMeta section = loSections.get(i);
                    String sectionSchool = section.schoolId;

                    SLCSectionReferenceType sectionRef = getSectionRef(studentMeta.sectionIds.get(0), sectionSchool);// Reference
                    // to
                    // Section

                    SLCStudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);
                    SLCLearningObjectiveReferenceType loRef = new SLCLearningObjectiveReferenceType();
                    SLCLearningObjectiveIdentityType loIdentity = new SLCLearningObjectiveIdentityType();
                    LearningStandardId lsi = new LearningStandardId();
                    learningObjectiveIdCounter++;
                    lsi.setIdentificationCode(ID_PREFIX_LO + learningObjectiveIdCounter);

                    loIdentity.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
                    loIdentity.setObjective(loId);
                    loIdentity.setObjectiveGradeLevel(GradeLevelType.ADULT_EDUCATION);
                    loRef.setLearningObjectiveIdentity(loIdentity);

                    SLCStudentCompetency studentCompetency = StudentGradeGenerator.getStudentCompetency(ssaRef, loRef,
                            null);
                    CompetencyLevelDescriptorType competencyLevel = new CompetencyLevelDescriptorType();
                    // competencyLevel.setId(ID_PREFIX_CLD
                    // + randGenerator.nextInt(StudentGradeRelations.COMPETENCY_LEVEL_DESCRIPTOR));
                    // competencyLevel.setRef(competencyLevel);

                    // CompetencyLevelDescriptorType competencyLevelRef = new
                    // CompetencyLevelDescriptorType();
                    // competencyLevelRef.setRef(competencyLevel);

                    competencyLevel.setCodeValue("1");

                    // studentCompetency.setCompetencyLevel(competencyLevelRef);
                    studentCompetency.setCompetencyLevel(competencyLevel);
                    studentCompetency.setId(ID_PREFIX_LO + reportCardId + "_" + loId + "_" + studentId);

                    writer.marshal(studentCompetency);
                }

                List<String> scIds = reportCardMeta.getStudentCompetencyIds();
                List<SectionMeta> scoSections = reportCardMeta.getStudentCompetencyObjectiveSections();
                for (int i = 0; i < scIds.size(); i++) {
                    String scoId = scIds.get(i);
                    SectionMeta section = scoSections.get(i);
                    String sectionId = section.id;
                    String sectionSchool = section.schoolId;

                    SLCSectionReferenceType sectionRef = getSectionRef(studentMeta.sectionIds.get(0), sectionSchool);// Reference
                    // to
                    // Section

                    SLCStudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);

                    SLCStudentCompetencyObjectiveReferenceType scoRef = new SLCStudentCompetencyObjectiveReferenceType();
                    SLCStudentCompetencyObjectiveIdentityType scoIdentity = new SLCStudentCompetencyObjectiveIdentityType();
                    scoIdentity.setStudentCompetencyObjectiveId(ID_PREFIX_SCO + reportCardId + "_" + scoId);
                    // lina
                    scoRef.setStudentCompetencyObjectiveIdentity(scoIdentity);

                    SLCLearningObjectiveReferenceType learningObjectiveRef = null;
                    SLCStudentCompetency studentCompetency = StudentGradeGenerator.getStudentCompetency(ssaRef,
                            learningObjectiveRef, scoRef);
                    CompetencyLevelDescriptorType competencyLevel = new CompetencyLevelDescriptorType();
                    // competencyLevel.setId(ID_PREFIX_CLD
                    // + randGenerator.nextInt(StudentGradeRelations.COMPETENCY_LEVEL_DESCRIPTOR));
                    //
                    // CompetencyLevelDescriptorType competencyLevelRef = new
                    // CompetencyLevelDescriptorType();
                    // competencyLevelRef.setRef(competencyLevel);
                    competencyLevel.setCodeValue("1");

                    // studentCompetency.setCompetencyLevel(competencyLevelRef);
                    studentCompetency.setCompetencyLevel(competencyLevel);

                    studentCompetency.setId(ID_PREFIX_SCO + reportCardId + "_" + scoId + "_" + studentId);

                    writer.marshal(studentCompetency);
                }
            }
        }
    }

    private static void generateDiploma(InterchangeWriter<InterchangeStudentGrade> writer) {

        String schoolId = MetaRelations.SCHOOL_MAP.entrySet().iterator().next().getKey();
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        edOrgIdentity.setStateOrganizationId(schoolId);
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        Diploma diploma = StudentGradeGenerator.getDiploma(schoolRef);

        writer.marshal(diploma);
    }

    private static void generateGradebookEntry(List<GradeBookEntryMeta> gradeBookEntryMetaList,
            Map<String, SectionMeta> sectionMetaMap, InterchangeWriter<InterchangeStudentGrade> writer) {

        for (int i = 0; i < gradeBookEntryMetaList.size(); i++) {
            GradeBookEntryMeta gradeBookEntryMeta = gradeBookEntryMetaList.get(i);

            SectionMeta section = gradeBookEntryMeta.getSection();
            String sectionId = section.id;
            String sectionSchool = section.schoolId;
            SLCSectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                         // to
                                                                                         // Section

            GradingPeriodMeta gpMeta = gradeBookEntryMeta.getGradingPeriod();
            SLCGradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(sectionSchool, gpMeta);

            SLCGradebookEntry gradeBookEntry = StudentGradeGenerator.getGradeBookEntry(gradingPeriodRef, sectionRef);
            gradeBookEntry.setId(gradeBookEntryMeta.getId());

            writer.marshal(gradeBookEntry);
        }
    }

    private static long generateStudentGradebookEntry(Map<String, StudentMeta> studentMetaMap,
            List<GradeBookEntryMeta> gradeBookEntryMetaList, Map<String, SectionMeta> sectionMetaMap,
            InterchangeWriter<InterchangeStudentGrade> writer) {
        long count = 0;

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;
            SLCStudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);

            for (int i = 0; i < gradeBookEntryMetaList.size(); i++) {

                // create a studentgradebookentry for just a fraction of gradebooks
                if (true /*
                          * (int) (Math.random() *
                          * StudentGradeRelations.INV_PROBABILITY_STUDENT_HAS_GRADEBOOKENTRY) == 1
                          */) {

                    GradeBookEntryMeta gradeBookEntryMeta = gradeBookEntryMetaList.get(i);

                    SectionMeta section = gradeBookEntryMeta.getSection();
                    String sectionId = section.id;

                    // need to ensure that this section is associated with the student
                    if (studentMeta.sectionIds.contains(sectionId)) {
                        String sectionSchool = section.schoolId;
                        SLCSectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                                     // to
                                                                                                     // Section

                        SLCStudentGradebookEntry studentGradeBookEntry = StudentGradeGenerator
                                .getStudentGradebookEntry(sectionRef, studentRef);

                        SLCGradebookEntryIdentityType identity = new SLCGradebookEntryIdentityType();
                        identity.setGradebookEntryType(gradeBookEntryMeta.getGradebookEntryType());
                        identity.setDateAssigned(gradeBookEntryMeta.getDateAssigned());
                        identity.setSectionReference(sectionRef);

                        SLCGradebookEntryReferenceType ref = new SLCGradebookEntryReferenceType();
                        ref.setGradebookEntryIdentity(identity);

                        studentGradeBookEntry.setGradebookEntryReference(ref);

                        writer.marshal(studentGradeBookEntry);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static void generateCompentencyLevelDescriptor(List<String> competencyLevelDescriptorIds,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        int i = 0;
        for (String cldId : competencyLevelDescriptorIds) {
            CompetencyLevelDescriptor competencyLevelDescriptor = new CompetencyLevelDescriptor();
            competencyLevelDescriptor.setCodeValue("CLD-CodeValue-" + cldId);
            competencyLevelDescriptor.setDescription("CompetencyLevelDesciptor Description " + cldId);
            competencyLevelDescriptor.setId(ID_PREFIX_CLD + (i++));
            competencyLevelDescriptor.setPerformanceBaseConversion(PerformanceBaseType.ADVANCED);

            writer.marshal(competencyLevelDescriptor);
        }
    }

    private static void generateLearningObjective(List<ReportCardMeta> reportCardMetas,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        for (ReportCardMeta reportCardMeta : reportCardMetas) {
            for (String loId : reportCardMeta.getLearningObjectiveIds()) {
                SLCLearningObjective lo = new SLCLearningObjective();
                lo.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
                lo.setDescription("Learning Objective Description");
                lo.setObjective(loId);
                lo.setObjectiveGradeLevel(GradeLevelType.ADULT_EDUCATION);

                writer.marshal(lo);
            }
        }
    }

    private static void generateStudentCompentencyObjective(List<ReportCardMeta> reportCardMetas,
            InterchangeWriter<InterchangeStudentGrade> writer) {

        String schoolId = MetaRelations.SCHOOL_MAP.entrySet().iterator().next().getKey();
        for (ReportCardMeta reportCardMeta : reportCardMetas) {
            String reportCardId = reportCardMeta.getId();
            for (String scoId : reportCardMeta.getStudentCompetencyIds()) {
                SLCEducationalOrgReferenceType edOrgRef = getEducationalOrgRef(schoolId);
                SLCStudentCompetencyObjective sco = StudentCompetancyObjectiveGenerator.getStudentCompetencyObjective(
                        ID_PREFIX_SCO + reportCardId + "_" + scoId, edOrgRef);

                writer.marshal(sco);
            }
        }
    }

}
