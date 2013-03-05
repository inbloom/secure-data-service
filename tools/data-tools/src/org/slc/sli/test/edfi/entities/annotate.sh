#!/bin/bash
for file in InterchangeAssessmentMetadata AssessmentFamily SLCAssessment AssessmentPeriodDescriptor PerformanceLevelDescriptor SLCObjectiveAssessment SLCAssessmentItem SLCLearningObjective LearningStandard InterchangeEducationOrgCalendar SLCSession SLCGradingPeriod SLCCalendarDate SLCAcademicWeek InterchangeEducationOrganization SLCStateEducationAgency SLCEducationServiceCenter FeederSchoolAssociation SLCLocalEducationAgency SLCSchool Location ClassPeriod SLCCourse CompetencyLevelDescriptor SLCProgram InterchangeHSGeneratedStudentTranscript Student SLCStudentSchoolAssociation StudentAcademicRecordExtendedType SLCCourseTranscript SLCReportCard SLCGrade SLCStudentSectionAssociation SLCSection SLCCourseOffering SLCCourse Diploma StudentAssessment SLCSchool SubmissionCertification InterchangeMasterSchedule SLCCourseOffering SLCSection SLCBellSchedule SLCMeetingTimeReferenceType InterchangeStaffAssociation Staff StaffEducationOrgEmploymentAssociation SLCStaffEducationOrgAssignmentAssociation Teacher SLCTeacherSchoolAssociation SLCTeacherSectionAssociation LeaveEvent OpenStaffPosition CredentialFieldDescriptor SLCStaffProgramAssociation InterchangeStudent Student InterchangeStudentAssessment StudentReferenceType AssessmentReferenceType SLCStudentAssessment SLCStudentObjectiveAssessment SLCStudentAssessmentItem InterchangeStudentAttendance SLCAttendanceEvent InterchangeStudentCohort SLCCohort SLCStudentCohortAssociation SLCStaffCohortAssociation InterchangeStudentDiscipline SLCDisciplineIncident SLCStudentDisciplineIncidentAssociation SLCDisciplineAction BehaviorDescriptor DisciplineDescriptor InterchangeStudentEnrollment SLCStudentSchoolAssociation SLCStudentSectionAssociation SLCGraduationPlan InterchangeStudentGrade SLCStudentAcademicRecord SLCCourseTranscript SLCReportCard SLCGrade SLCStudentCompetency Diploma SLCGradebookEntry SLCStudentGradebookEntry CompetencyLevelDescriptor SLCLearningObjective SLCStudentCompetencyObjective InterchangeStudentParent Student Parent SLCStudentParentAssociation InterchangeStudentProgram SLCStudentProgramAssociation StudentSpecialEdProgramAssociation RestraintEvent StudentCTEProgramAssociation StudentTitleIPartAProgramAssociation ServiceDescriptor CalendarDate
do
    if [ -f "$file.java" ] 
    then
        re=`grep -c @XmlRootElement "$file.java"`
    if [ "$re" -lt 1 ]
    then 
        sed -i 's/public class/@XmlRootElement \rpublic class/' "$file.java"
        sed -i 's/package org.slc.sli.test.edfi.entities;/package org.slc.sli.test.edfi.entities;\rimport javax.xml.bind.annotation.XmlRootElement;/' "$file.java"
    fi
    else

        echo "File Not Exists! $file.java" 
    fi
done

