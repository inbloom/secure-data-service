package org.slc.sli.test.generators.interchange;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptor;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptorType;
import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.CourseIdentityType;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.CourseTranscript;
import org.slc.sli.test.edfi.entities.Diploma;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.Grade;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradebookEntry;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.InterchangeStudentGrade;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.ReportCard;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.StudentAcademicRecord;
import org.slc.sli.test.edfi.entities.StudentCompetency;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentGradebookEntry;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.GradeBookEntryMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.ReportCardMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.meta.relations.StudentGradeRelations;
import org.slc.sli.test.generators.StudentCompetancyObjectiveGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentGradeGenerator;
import org.slc.sli.test.utils.JaxbUtils;

/**
 * Generates the StudentGradeGrade Interchange
 *
 * @author ldalgado
 *
 */
public final class InterchangeStudentGradeGenerator {

    private static ObjectFactory factory = new ObjectFactory();
    private static final String ID_PREFIX_STUDENT_ACADEMIC_RECORD = "StudentAcademicRecord_";
    private static final String ID_PREFIX_COURSE_TRANSCRIPT = "CourseTranscript_";
    private static final String ID_PREFIX_REPORT_CARD = "ReportCard_";
    private static final String ID_PREFIX_GRADE = "Grade_";
    private static final String ID_PREFIX_LO = "LearningObjective_";
    private static final String ID_PREFIX_SCO = "SCO_";
    private static final String ID_PREFIX_CLD = "CLD_";
    private static final Random randGenerator = new Random();

    // LightWeight object that holds id of the reference.
    static final class Ref extends ComplexObjectType {

        public Ref(String id) {

            this.id = id;
        }
    }

    private static GradingPeriodReferenceType getGradingPeriodRef(String schoolId, GradingPeriodMeta gpMeta) {

        GradingPeriodReferenceType gradingPeriodRef = new GradingPeriodReferenceType();
        GradingPeriodIdentityType gradingPeriodItentity = new GradingPeriodIdentityType();
        gradingPeriodRef.setGradingPeriodIdentity(gradingPeriodItentity);
        gradingPeriodItentity.setSchoolYear(gpMeta.getBeginData() + "-" + gpMeta.getEndDate());
        gradingPeriodItentity.setGradingPeriod(GradingPeriodType.END_OF_YEAR);
        gradingPeriodItentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        return gradingPeriodRef;
    }

    private static EducationalOrgReferenceType getEducationalOrgRef(String schoolId) {

        EducationalOrgReferenceType edOrgRef = new EducationalOrgReferenceType();// References to
                                                                                 // EducationalOrg
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        EducationOrgIdentificationCode edOrgCode = new EducationOrgIdentificationCode();
        edOrgCode.setID(schoolId);
        edOrgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgCode);
        edOrgIdentity.getEducationOrgIdentificationCode().add(edOrgCode);
        edOrgRef.setEducationalOrgIdentity(edOrgIdentity);
        return edOrgRef;
    }

    private static SectionReferenceType getSectionRef(String sectionId, String schoolId) {

        SectionReferenceType sectionRef = new SectionReferenceType();
        SectionIdentityType sectionIdentity = new SectionIdentityType();
        sectionIdentity.setUniqueSectionCode(sectionId);
        sectionRef.setSectionIdentity(sectionIdentity);
        sectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        return sectionRef;
    }

    public static void generate(XMLStreamWriter writer) throws XMLStreamException {

        StudentGradeRelations.buildCompetencyLevelDescriptorMeta();
        StudentGradeRelations.buildGradeBookEntriesMeta();
        StudentGradeRelations.buildReportCardMeta();
        InterchangeStudentGrade interchange = new InterchangeStudentGrade();
        List<ComplexObjectType> interchangeObjects = interchange
                .getStudentAcademicRecordOrCourseTranscriptOrReportCard();
        
        writer.writeStartElement("InterchangeStudentGrade");
        writer.writeNamespace(null, "http://ed-fi.org/0100");
        
        writeEntitiesToInterchange(interchangeObjects, writer);

        writer.writeEndElement();
    }

    private static void writeEntitiesToInterchange(List<ComplexObjectType> interchangeObjects, 
            XMLStreamWriter writer) {

        int studentCount = MetaRelations.STUDENT_MAP.size();
        long count = 0;

        generateStudentAcademicRecord(MetaRelations.STUDENT_MAP, MetaRelations.SESSION_MAP,
                MetaRelations.SECTION_MAP, StudentGradeRelations.REPORT_CARD_META, writer);
        System.out.println("Finished StudentAcademicRecord [" + studentCount + "] Records Generated");
        generateCourseTranscript(MetaRelations.STUDENT_MAP, MetaRelations.SESSION_MAP,
                MetaRelations.COURSE_MAP, MetaRelations.COURSES_PER_STUDENT, writer);
        System.out.println("Finished CourseTranscript [" + studentCount * MetaRelations.COURSES_PER_STUDENT
                + "] Records Generated");
        generateReportCard(MetaRelations.STUDENT_MAP, StudentGradeRelations.REPORT_CARD_META,
                MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished ReportCard [" + studentCount * StudentGradeRelations.REPORT_CARDS
                + "] Records Generated");
        generateGrade(MetaRelations.STUDENT_MAP, StudentGradeRelations.REPORT_CARD_META,
                MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished Grade [" + studentCount * MetaRelations.COURSES_PER_STUDENT
                * MetaRelations.SECTIONS_PER_COURSE_SESSION + "] Records Generated");
        generateStudentCompetency(MetaRelations.STUDENT_MAP,
                StudentGradeRelations.REPORT_CARD_META, MetaRelations.SECTION_MAP, writer);
        System.out
                .println("Finished StudentCompetency ["
                        + studentCount
                        * StudentGradeRelations.REPORT_CARDS
                        * (StudentGradeRelations.LEARNING_OBJECTIVES_PER_REPORT + StudentGradeRelations.STUDENT_COMPETENCY_OBJECTIVE_PER_REPORT)
                        + "] Records Generated");
        generateDiploma(writer);
        System.out.println("Finished Diploma [O] Records Generated ");
        generateGradebookEntry(StudentGradeRelations.GRADE_BOOK_ENTRY_METAS,
                MetaRelations.SECTION_MAP, writer);
        System.out.println("Finished GradebookEntry [" + StudentGradeRelations.GRADEBOOK_ENTRIES
                + "] Records Generated");
        count = generateStudentGradebookEntry(MetaRelations.STUDENT_MAP,
                StudentGradeRelations.GRADE_BOOK_ENTRY_METAS, MetaRelations.SECTION_MAP, writer);
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

    private static void generateStudentAcademicRecord(
            Map<String, StudentMeta> studentMetaMap, Map<String, SessionMeta> sessionMetaMap,
            Map<String, SectionMeta> sectionMetaMap, List<ReportCardMeta> reportCardsForStudent, XMLStreamWriter writer) {

        String sessionId = sessionMetaMap.keySet().iterator().next();
        SessionReferenceType sessionRef = new SessionReferenceType();// Reference to First Session.
                                                                     // All Academic Records are for
                                                                     // the first Session in
                                                                     // SessionMetaMap
        SessionIdentityType sessionIdentity = new SessionIdentityType();
        sessionIdentity.setSessionName(sessionId);
        sessionRef.setSessionIdentity(sessionIdentity);

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;
            StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);// Reference
                                                                                                  // to
                                                                                                  // Student

            List<ReferenceType> reportCardRefs = new ArrayList<ReferenceType>();
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                ReferenceType reportCardRef = new ReferenceType();
                reportCardRef.setRef(new Ref(studentId + "_" + ID_PREFIX_REPORT_CARD + reportCardMeta.getId()));
                reportCardRefs.add(reportCardRef);// References to ReportCards
            }

            ReferenceType diplomaRef = null;// Reference to Diploma. Not used
            StudentAcademicRecord sar = StudentGradeGenerator.getStudentAcademicRecord(studentRef, sessionRef,
                    reportCardRefs, diplomaRef);
            sar.setId(ID_PREFIX_STUDENT_ACADEMIC_RECORD + studentId);

            JaxbUtils.marshal(sar, writer);
        }
    }

    private static void generateCourseTranscript(
            Map<String, StudentMeta> studentMetaMap, Map<String, SessionMeta> sessionMetaMap,
            Map<String, CourseMeta> courseMetaMap, int coursesPerStudent, XMLStreamWriter writer) {

        List<String> courseSet = new LinkedList<String>(courseMetaMap.keySet());
        courseSet = courseSet.subList(0, coursesPerStudent);// Every Student has a CourseTranscript
                                                            // for the first N Courses in
                                                            // CourseMetaMap

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            for (String cId : courseSet) {
                CourseMeta courseMeta = courseMetaMap.get(cId);
                String courseId = courseMeta.id;
                String courseSchool = courseMeta.schoolId;

                CourseReferenceType courseRef = new CourseReferenceType();// References to Course
                CourseIdentityType courseIdentity = new CourseIdentityType();
                CourseCode courseCode = new CourseCode();
                courseCode.setID(courseId);
                courseCode.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
                courseIdentity.getCourseCode().add(courseCode);
                courseRef.setCourseIdentity(courseIdentity);

                ReferenceType sarReference = new ReferenceType();// Reference to
                                                                 // StudentAcademicRecord
                sarReference.setRef(new Ref(ID_PREFIX_STUDENT_ACADEMIC_RECORD + studentId));

                EducationalOrgReferenceType edOrgRef = getEducationalOrgRef(courseSchool);// Reference
                                                                                          // to
                                                                                          // EducationalOrg
                CourseTranscript courseTranscript = StudentGradeGenerator.getCourseTranscript(courseRef, sarReference,
                        edOrgRef);
                courseTranscript.setId(ID_PREFIX_COURSE_TRANSCRIPT + courseId + "_" + studentId);
                JaxbUtils.marshal(courseTranscript, writer);
            }
        }
    }

    private static void generateReportCard(
            Map<String, StudentMeta> studentMetaMap, List<ReportCardMeta> reportCardsForStudent,
            Map<String, SectionMeta> sectionMetaMap, XMLStreamWriter writer) {

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;
            String schoolId = studentMeta.schoolIds.get(0);

            StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                String reportCardId = reportCardMeta.getId();
                List<ReferenceType> gradeReferences = new ArrayList<ReferenceType>();// References
                                                                                     // to Grades.
                                                                                     // One Grade
                                                                                     // per
                                                                                     // Section.N
                                                                                     // Sections per
                                                                                     // Course

                List<String> sectionSet = new LinkedList<String>(sectionMetaMap.keySet());
                sectionSet = sectionSet.subList(0, MetaRelations.COURSES_PER_STUDENT
                        * MetaRelations.SECTIONS_PER_COURSE_SESSION);

                // One Grade per Section. N Sections per Course. N Courses per Student. We will use
                // the first N Sections
                for (String sectionId : sectionSet) {
                    ReferenceType gradeReference = new ReferenceType();
                    gradeReference.setRef(new Ref(ID_PREFIX_GRADE + reportCardId + sectionId + studentId));
                    gradeReferences.add(gradeReference);
                }

                // References to StudentCompetency(For LearningObjectives and
                // StudentCompetencyObjectives)
                List<ReferenceType> scReferences = new ArrayList<ReferenceType>();
                List<String> loIds = reportCardMeta.getLearningObjectiveIds();
                List<String> scIds = reportCardMeta.getStudentCompetencyIds();

                for (String loId : loIds) {
                    ReferenceType loRef = new ReferenceType();
                    loRef.setRef(new Ref(ID_PREFIX_LO + reportCardId + "_" + loId + "_" + studentId));
                    scReferences.add(loRef);
                }

                for (String scId : scIds) {
                    ReferenceType scoRef = new ReferenceType();
                    scoRef.setRef(new Ref(ID_PREFIX_SCO + reportCardId + "_" + scId + "_" + studentId));
                    scReferences.add(scoRef);
                }

                GradingPeriodMeta gpMeta = reportCardMeta.getGradingPeriod();// Reference to
                                                                             // GradingPeriod
                GradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(schoolId, gpMeta);

                ReportCard reportCard = StudentGradeGenerator.getReportCard(studentRef, gradingPeriodRef,
                        gradeReferences, scReferences);
                reportCard.setId(studentId + "_" + ID_PREFIX_REPORT_CARD + reportCardMeta.getId());
                
                JaxbUtils.marshal(reportCard, writer);
            }
        }
    }

    private static void generateGrade(
            Map<String, StudentMeta> studentMetaMap, List<ReportCardMeta> reportCardsForStudent,
            Map<String, SectionMeta> sectionMetaMap, XMLStreamWriter writer) {

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {

                String reportCardId = reportCardMeta.getId();
                List<String> sectionSet = new LinkedList<String>(sectionMetaMap.keySet());
                sectionSet = sectionSet.subList(0, MetaRelations.COURSES_PER_STUDENT
                        * MetaRelations.SECTIONS_PER_COURSE_SESSION);

                for (String sectionId : sectionSet) {// One Grade per Section. N Sections per
                                                     // Course. N Courses per Student. We will use
                                                     // the first N Sections
                    String schoolId = sectionMetaMap.get(sectionId).schoolId;

                    SectionReferenceType sectionRef = getSectionRef(sectionId, schoolId);// Reference
                                                                                         // to
                                                                                         // Section

                    GradingPeriodMeta gpMeta = reportCardMeta.getGradingPeriod();
                    GradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(schoolId, gpMeta);

                    StudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);// Reference
                                                                                           // to
                                                                                           // StudentSectionAssociation
                    Grade grade = StudentGradeGenerator.getGrade(ssaRef, gradingPeriodRef);
                    grade.setId(ID_PREFIX_GRADE + reportCardId + sectionId + studentId);
                    
                    JaxbUtils.marshal(grade, writer);
                }
            }
        }
    }

    private static void generateStudentCompetency(
            Map<String, StudentMeta> studentMetaMap, List<ReportCardMeta> reportCardsForStudent,
            Map<String, SectionMeta> sectionMetaMap, XMLStreamWriter writer) {

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;

            StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);
            for (ReportCardMeta reportCardMeta : reportCardsForStudent) {
                String reportCardId = reportCardMeta.getId();

                List<String> loIds = reportCardMeta.getLearningObjectiveIds();
                List<SectionMeta> loSections = reportCardMeta.getLearningObjectiveSections();
                for (int i = 0; i < loIds.size(); i++) {

                    String loId = loIds.get(i);
                    SectionMeta section = loSections.get(i);
                    String sectionId = section.id;
                    String sectionSchool = section.schoolId;

                    SectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                              // to
                                                                                              // Section

                    StudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);
                    LearningObjectiveReferenceType loRef = new LearningObjectiveReferenceType();
                    LearningObjectiveIdentityType loIdentity = new LearningObjectiveIdentityType();
                    loIdentity.getLearningObjectiveIdOrObjective().add(ID_PREFIX_LO + reportCardId + "_" + loId);
                    loRef.setLearningObjectiveIdentity(loIdentity);

                    StudentCompetency studentCompetency = StudentGradeGenerator.getStudentCompetency(ssaRef, loRef,
                            null);
                    CompetencyLevelDescriptorType competencyLevel = new CompetencyLevelDescriptorType();
                    competencyLevel.setId(ID_PREFIX_CLD
                            + randGenerator.nextInt(StudentGradeRelations.COMPETENCY_LEVEL_DESCRIPTOR));
                    competencyLevel.setRef(competencyLevel);

                    CompetencyLevelDescriptorType competencyLevelRef = new CompetencyLevelDescriptorType();
                    competencyLevelRef.setRef(competencyLevel);

                    studentCompetency.setCompetencyLevel(competencyLevelRef);
                    studentCompetency.setId(ID_PREFIX_LO + reportCardId + "_" + loId + "_" + studentId);
                    
                    JaxbUtils.marshal(studentCompetency, writer);
                }

                List<String> scIds = reportCardMeta.getStudentCompetencyIds();
                List<SectionMeta> scoSections = reportCardMeta.getStudentCompetencyObjectiveSections();
                for (int i = 0; i < scIds.size(); i++) {
                    String scoId = scIds.get(i);
                    SectionMeta section = scoSections.get(i);
                    String sectionId = section.id;
                    String sectionSchool = section.schoolId;

                    SectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                              // to
                                                                                              // Section

                    StudentSectionAssociationReferenceType ssaRef = StudentGradeGenerator
                            .getStudentSectionAssociationReference(studentRef, sectionRef);

                    StudentCompetencyObjectiveReferenceType scoRef = new StudentCompetencyObjectiveReferenceType();
                    StudentCompetencyObjectiveIdentityType scoIdentity = new StudentCompetencyObjectiveIdentityType();
                    JAXBElement<String> oid = factory
                            .createStudentCompetencyObjectiveIdentityTypeStudentCompetencyObjectiveId(ID_PREFIX_SCO
                                    + reportCardId + "_" + scoId);
                    scoIdentity.getStudentCompetencyObjectiveIdOrObjective().add(oid);

                    LearningObjectiveReferenceType learningObjectiveRef = null;
                    StudentCompetency studentCompetency = StudentGradeGenerator.getStudentCompetency(ssaRef,
                            learningObjectiveRef, scoRef);
                    CompetencyLevelDescriptorType competencyLevel = new CompetencyLevelDescriptorType();
                    competencyLevel.setId(ID_PREFIX_CLD
                            + randGenerator.nextInt(StudentGradeRelations.COMPETENCY_LEVEL_DESCRIPTOR));

                    CompetencyLevelDescriptorType competencyLevelRef = new CompetencyLevelDescriptorType();
                    competencyLevelRef.setRef(competencyLevel);

                    studentCompetency.setCompetencyLevel(competencyLevelRef);

                    studentCompetency.setId(ID_PREFIX_SCO + reportCardId + "_" + scoId + "_" + studentId);
                    
                    JaxbUtils.marshal(studentCompetency, writer);
                }
            }
        }
    }

    private static void generateDiploma(XMLStreamWriter writer) {

        String schoolId = MetaRelations.SCHOOL_MAP.entrySet().iterator().next().getKey();
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        edOrgIdentity.setStateOrganizationId(schoolId);
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        Diploma diploma = StudentGradeGenerator.getDiploma(schoolRef);
        
        JaxbUtils.marshal(diploma, writer);
    }

    private static void generateGradebookEntry(List<GradeBookEntryMeta> gradeBookEntryMetaList, 
            Map<String, SectionMeta> sectionMetaMap, XMLStreamWriter writer) {

        for (int i = 0; i < gradeBookEntryMetaList.size(); i++) {
            GradeBookEntryMeta gradeBookEntryMeta = gradeBookEntryMetaList.get(i);

            SectionMeta section = gradeBookEntryMeta.getSection();
            String sectionId = section.id;
            String sectionSchool = section.schoolId;
            SectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                      // to Section

            GradingPeriodMeta gpMeta = gradeBookEntryMeta.getGradingPeriod();
            GradingPeriodReferenceType gradingPeriodRef = getGradingPeriodRef(sectionSchool, gpMeta);

            GradebookEntry gradeBookEntry = StudentGradeGenerator.getGradeBookEntry(gradingPeriodRef, sectionRef);
            gradeBookEntry.setId(gradeBookEntryMeta.getId());

            JaxbUtils.marshal(gradeBookEntry, writer);           
        }
    }

    private static long generateStudentGradebookEntry(
            Map<String, StudentMeta> studentMetaMap, List<GradeBookEntryMeta> gradeBookEntryMetaList,
            Map<String, SectionMeta> sectionMetaMap, XMLStreamWriter writer) {
        long count = 0;

        for (StudentMeta studentMeta : studentMetaMap.values()) {
            String studentId = studentMeta.id;
            StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentId);

            for (int i = 0; i < gradeBookEntryMetaList.size(); i++) {

                // create a studentgradebookentry for just a fraction of gradebooks
                if (true /*
                          * (int) (Math.random() *
                          * StudentGradeRelations.INV_PROBABILITY_STUDENT_HAS_GRADEBOOKENTRY) == 1
                          */) {

                    GradeBookEntryMeta gradeBookEntryMeta = gradeBookEntryMetaList.get(i);
                    SectionMeta section = gradeBookEntryMeta.getSection();
                    String sectionId = section.id;
                    String sectionSchool = section.schoolId;
                    SectionReferenceType sectionRef = getSectionRef(sectionId, sectionSchool);// Reference
                                                                                              // to
                                                                                              // Section

                    StudentGradebookEntry studentGradeBookEntry = StudentGradeGenerator.getStudentGradebookEntry(
                            sectionRef, studentRef);
                    ReferenceType ref = new ReferenceType();
                    ref.setRef(new Ref(gradeBookEntryMeta.getId()));
                    studentGradeBookEntry.setGradebookEntryReference(ref);

                    JaxbUtils.marshal(studentGradeBookEntry, writer);           
                    count++;
                }
            }
        }
        return count;
    }

    private static void generateCompentencyLevelDescriptor(
            List<String> competencyLevelDescriptorIds, XMLStreamWriter writer) {

        int i = 0;
        for (String cldId : competencyLevelDescriptorIds) {
            CompetencyLevelDescriptor competencyLevelDescriptor = new CompetencyLevelDescriptor();
            competencyLevelDescriptor.setCodeValue("CLD-CodeValue-" + cldId);
            competencyLevelDescriptor.setDescription("CompetencyLevelDesciptor Description " + cldId);
            competencyLevelDescriptor.setId(ID_PREFIX_CLD + (i++));
            competencyLevelDescriptor.setPerformanceBaseConversion(PerformanceBaseType.ADVANCED);

            JaxbUtils.marshal(competencyLevelDescriptor, writer);           
        }
    }

    private static void generateLearningObjective(
            List<ReportCardMeta> reportCardMetas, XMLStreamWriter writer) {

        for (ReportCardMeta reportCardMeta : reportCardMetas) {
            String reportCardId = reportCardMeta.getId();
            for (String loId : reportCardMeta.getLearningObjectiveIds()) {
                LearningObjective lo = new LearningObjective();
                lo.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
                lo.setDescription("Learning Objective Description");
                lo.setObjective(ID_PREFIX_LO + reportCardId + "_" + loId);
                lo.setObjectiveGradeLevel(GradeLevelType.ADULT_EDUCATION);

                JaxbUtils.marshal(lo, writer);           
            }
        }
    }

    private static void generateStudentCompentencyObjective(
            List<ReportCardMeta> reportCardMetas, XMLStreamWriter writer) {

        String schoolId = MetaRelations.SCHOOL_MAP.entrySet().iterator().next().getKey();
        for (ReportCardMeta reportCardMeta : reportCardMetas) {
            String reportCardId = reportCardMeta.getId();
            for (String scoId : reportCardMeta.getStudentCompetencyIds()) {
                EducationalOrgReferenceType edOrgRef = getEducationalOrgRef(schoolId);
                StudentCompetencyObjective sco = StudentCompetancyObjectiveGenerator.getStudentCompetencyObjective(
                        ID_PREFIX_SCO + reportCardId + "_" + scoId, edOrgRef);

                JaxbUtils.marshal(sco, writer);           
            }
        }
    }

}
