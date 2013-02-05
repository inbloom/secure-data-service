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


package org.slc.sli.test.exportTool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateCourse {
    private String begin = new StringBuilder()
    .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    .append("<InterchangeEducationOrganization xsi:schemaLocation=\"http://ed-fi.org/0100 Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n")
    .toString();

    private String end = new StringBuilder()
    .append("</InterchangeEducationOrganization>\n")
    .toString();

    private String body = new StringBuilder()
//    .append("   <Course id=\"ID_1\">\n")
    .append("   <Course>\n")
    .append("       <CourseTitle>--CourseTitle--</CourseTitle>\n")
    .append("       <NumberOfParts>--NumberOfParts--</NumberOfParts>\n")

    .append("--CourseCodes--\n")

    .append("       <CourseLevel>--CourseLevel--</CourseLevel>\n")
    .append("       <CourseLevelCharacteristics>\n")

    .append("--CourseLevelCharacteristics--\n")

    .append("       </CourseLevelCharacteristics>\n")
    .append("       <GradesOffered>\n")

    .append("--GradeLevels--\n")

    .append("       </GradesOffered>\n")
    .append("       <SubjectArea>--SubjectArea--</SubjectArea>\n")
    .append("       <CourseDescription>--CourseDescription--</CourseDescription>\n")
    .append("       <DateCourseAdopted>--DateCourseAdopted--</DateCourseAdopted>\n")
    .append("       <HighSchoolCourseRequirement>--HighSchoolCourseRequirement--</HighSchoolCourseRequirement>\n")
    .append("       <CourseGPAApplicability>--CourseGPAApplicability--</CourseGPAApplicability>\n")
    .append("       <CourseDefinedBy>--CourseDefinedBy--</CourseDefinedBy>\n")
    .append("       <MinimumAvailableCredit CreditType=\"--MiniCreditType--\" CreditConversion=\"--MiniCreditConversion--\">\n")
    .append("           <Credit>--MiniCredit--</Credit>\n")
    .append("       </MinimumAvailableCredit>\n")
    .append("       <MaximumAvailableCredit CreditType=\"--MaxCreditType--\" CreditConversion=\"--MaxCreditConversion--\">\n")
    .append("           <Credit>--MaxCredit--</Credit>\n")
    .append("       </MaximumAvailableCredit>\n")
    .append("       <CareerPathway>--CareerPathway--</CareerPathway>\n")
//    .append("       <EducationOrganizationReference id=\"ID_2\" ref=\"ID_1\">\n")
    .append("       <EducationOrganizationReference>\n")
    .append("           <EducationalOrgIdentity>\n")
    .append("               <StateOrganizationId>--StateOrganizationId--</StateOrganizationId>\n")
    .append("           </EducationalOrgIdentity>\n")
    .append("       </EducationOrganizationReference>\n")

    .append("--LearningStandardReferences--\n")

    .append("--LearningObjectiveReferences--\n")

//    .append("       <CompetencyLevels id=\"ID_5\" ref=\"ID_4\">\n")
    .append("       <CompetencyLevels>\n")
    .append("           <CodeValue>--CompetencyLevelsCodeValue--</CodeValue>\n")
    .append("       </CompetencyLevels>\n")
    .append("   </Course>\n")
    .toString();

    private List<String> bodyKeyList = new ArrayList<String>(
            Arrays.asList(new String[]{"CourseTitle", "NumberOfParts", "CourseLevel", "SubjectArea", "CourseDescription",
                    "DateCourseAdopted", "HighSchoolCourseRequirement", "CourseGPAApplicability", "CourseDefinedBy",
                    "MiniCreditType", "MiniCreditConversion", "MiniCredit", "MaxCreditType", "MaxCreditConversion",
                    "MaxCredit", "CareerPathway", "StateOrganizationId", "CompetencyLevelsCodeValue"}));

    private String courseCodes = new StringBuilder()
    .append("       <CourseCode IdentificationSystem=\"--IdentificationSystem--\" AssigningOrganizationCode=\"--AssigningOrganizationCode--\">\n")
    .append("           <ID>--CourseCodeID--</ID>\n")
    .append("       </CourseCode>\n")
    .toString();

    private List<String> courseCodesKeyList = new ArrayList<String>(
            Arrays.asList(new String[]{"IdentificationSystem", "AssigningOrganizationCode", "CourseCodeID"}));

    private List<String> courseCodesJoinKeys = new ArrayList<String>(
            Arrays.asList(new String[]{"EducationOrganizationId", "IdentityCourseCode"}));

    private String courseLevelCharacteristics = new StringBuilder()
    .append("           <CourseLevelCharacteristic>--CourseLevelCharacteristic--</CourseLevelCharacteristic>\n")
    .toString();

    private List<String> courseLevelCharacteristicsKeyList = new ArrayList<String>(
            Arrays.asList(new String[]{"CourseLevelCharacteristic"}));

    private List<String> courseLevelCharacteristicsJoinKeys = new ArrayList<String>(
            Arrays.asList(new String[]{"EducationOrganizationId", "IdentityCourseCode"}));

    private String gradeLevels = new StringBuilder()
    .append("           <GradeLevel>--GradeLevel--</GradeLevel>\n")
    .toString();

    private List<String> gradeLevelsKeyList = new ArrayList<String>(
            Arrays.asList(new String[]{"GradeLevel"}));

    private List<String> gradeLevelsJoinKeys = new ArrayList<String>(
            Arrays.asList(new String[]{"EducationOrganizationId", "IdentityCourseCode"}));

//    private String stateOrganizationIds = new StringBuilder()
//    .toString();

    private String learningStandardReferences = new StringBuilder()
//    .append("       <LearningStandardReference id=\"ID_3\" ref=\"ID_1\">\n")
    .append("       <LearningStandardReference>\n")
    .append("           <LearningStandardIdentity>\n")
    .append("               <LearningStandardId ContentStandardName=\"--LearningStandardIdContentStandardName--\">\n")
    .append("                   <IdentificationCode>--LearningStandardIdIdentificationCode--</IdentificationCode>\n")
    .append("               </LearningStandardId>\n")
    .append("           </LearningStandardIdentity>\n")
    .append("       </LearningStandardReference>\n")
    .toString();
    
    private List<String> learningStandardReferencesKeyList = new ArrayList<String>(
            Arrays.asList(new String[]{"LearningStandardIdContentStandardName", "LearningStandardIdIdentificationCode"}));
    
    private List<String> learningStandardReferencesJoinKeys = new ArrayList<String>(
            Arrays.asList(new String[]{"EducationOrganizationId", "IdentityCourseCode"}));

    private String learningObjectiveReferences = new StringBuilder()
//    .append("       <LearningObjectiveReference id=\"ID_4\" ref=\"ID_4\">\n")
    .append("       <LearningObjectiveReference>\n")
    .append("           <LearningObjectiveIdentity>\n")
    .append("               <LearningObjectiveId ContentStandardName=\"--LearningObjectiveIdContentStandardName--\">\n")
    .append("                   <IdentificationCode>--LearningObjectiveIdIdentificationCode--</IdentificationCode>\n")
    .append("               </LearningObjectiveId>\n")
    .append("           </LearningObjectiveIdentity>\n")
    .append("       </LearningObjectiveReference>\n")
    .toString();

    private String competencyLevelses = new StringBuilder()
    .toString();


    private ResultSet coursesResultSet;
    private String coursesQuery = new StringBuilder()
    .append("SELECT c.EducationOrganizationId\n")
    .append("     ,c.EducationOrganizationId as StateOrganizationId\n")
    .append("      ,c.IdentityCourseCode\n")
    .append("      ,c.CourseTitle\n")
    .append("      ,c.NumberOfParts\n")
    .append("--      ,c.CourseLevelTypeId\n")
    .append("      ,clt.CodeValue as CourseLevel\n")
    .append("--      ,c.SubjectAreaTypeId\n")
    .append("      ,ast.CodeValue as SubjectArea\n")
    .append("      ,c.CourseDescription\n")
    .append("      ,c.DateCourseAdopted\n")
    .append("      ,c.HighSchoolCourseRequirement\n")
    .append("--      ,c.CourseGPAApplicabilityTypeId\n")
    .append("      ,cgpaat.CodeValue as CourseGPAApplicability\n")
    .append("      ,c.CourseDefinedByTypeId as CourseDefinedBy\n")
    .append("      ,c.MinimumAvailableCreditTypeId as MiniCreditType\n")
    .append("      ,null as MiniCreditType\n")
    .append("      ,c.MinimumAvailableCredit as MiniCredit\n")
    .append("      ,null as MiniCreditConversion\n")
    .append("      ,c.MaximumAvailableCreditTypeId as MaxCreditType\n")
    .append("      ,null as MaxCreditConversion\n")
    .append("      ,c.MaximumAvailableCredit as MaxCredit\n")
    .append("      ,c.CareerPathwayTypeId as CareerPathway\n")
    .append("--      ,c.CompetencyLevelDescriptorId\n")
    .append("      ,cld.CodeValue as CompetencyLevelsCodeValue\n")
    .append("  FROM EdFi.edfi.Course c\n")
    .append("  LEFT JOIN EdFi.edfi.CourseLevelType clt ON c.CourseLevelTypeId = clt.CourseLevelTypeId\n")
    .append("  LEFT JOIN EdFi.edfi.AcademicSubjectType ast ON c.SubjectAreaTypeId = ast.AcademicSubjectTypeId\n")
    .append("  LEFT JOIN EdFi.edfi.CourseGPAApplicabilityType cgpaat ON c.CourseGPAApplicabilityTypeId = cgpaat.CourseGPAApplicabilityTypeId\n")
    .append("  LEFT JOIN EdFi.edfi.CompetencyLevelDescriptor cld ON c.CompetencyLevelDescriptorId = cld.CompetencyLevelDescriptorId\n")
    .append("  ORDER BY c.EducationOrganizationId, c.IdentityCourseCode\n")
    .toString();

    private ResultSet courseCodesResultSet;
    private String courseCodesQuery = new StringBuilder()
    .append("SELECT c.EducationOrganizationId\n")
    .append("      ,c.IdentityCourseCode\n")
    .append("      --,ccic.CourseCodeSystemTypeId\n")
    .append("      ,ccst.CodeValue as IdentificationSystem\n")
    .append("      ,ccic.AssigningOrganizationCode as AssigningOrganizationCode\n")
    .append("      ,ccic.IdentificationCode as CourseCodeID\n")
    .append("  FROM EdFi.edfi.CourseCodeIdentificationCode ccic\n")
    .append("  RIGHT JOIN EdFi.edfi.Course c \n")
    .append("   ON c.EducationOrganizationId = ccic.EducationOrganizationId\n")
    .append("      AND c.IdentityCourseCode = ccic.IdentityCourseCode\n")
    .append("  LEFT JOIN EdFi.edfi.CourseCodeSystemType ccst ON ccic.CourseCodeSystemTypeId = ccst.CourseCodeSystemTypeId\n")
    .append("  ORDER BY c.EducationOrganizationId, c.IdentityCourseCode\n")
    .toString();

    private ResultSet courseLevelCharacteristicsResultSet;
    private String courseLevelCharacteristicsQuery = new StringBuilder()
    .append("SELECT c.EducationOrganizationId\n")
    .append("      ,c.IdentityCourseCode\n")
    .append("      --,clc.CourseLevelCharacteristicsTypeId\n")
    .append("      ,clct.CodeValue as CourseLevelCharacteristic\n")
    .append("  FROM EdFi.edfi.CourseLevelCharacteristics clc\n")
    .append("  RIGHT JOIN EdFi.edfi.Course c \n")
    .append("   ON c.EducationOrganizationId = clc.EducationOrganizationId\n")
    .append("       AND c.IdentityCourseCode = clc.IdentityCourseCode\n")
    .append("  LEFT JOIN EdFi.edfi.CourseLevelCharacteristicsType clct ON clc.CourseLevelCharacteristicsTypeId = clct.CourseLevelCharacteristicsTypeId\n")
    .append("  ORDER BY c.EducationOrganizationId, c.IdentityCourseCode\n")
    .toString();

    private ResultSet gradeLevelsResultSet;
    private String gradeLevelsQuery = new StringBuilder()
    .append("SELECT cgo.EducationOrganizationId\n")
    .append("      ,cgo.IdentityCourseCode\n")
    .append("      --,cgo.GradesOfferedTypeId\n")
    .append("      ,glt.CodeValue as GradeLevel\n")
    .append("  FROM EdFi.edfi.CourseGradesOffered cgo\n")
    .append("  LEFT JOIN EdFi.edfi.GradeLevelType glt ON cgo.GradesOfferedTypeId = glt.GradeLevelTypeId\n")
    .append("  ORDER BY cgo.EducationOrganizationId, cgo.IdentityCourseCode\n")
    .toString();

    private ResultSet learningStandardReferencesResultSet;
    private String learningStandardReferencesQuery = new StringBuilder()
    .append("SELECT cls.EducationOrganizationId\n")
    .append("       ,cls.IdentityCourseCode\n")
    .append("       ,cls.LearningStandardId as LearningStandardIdIdentificationCode\n")
    .append("       ,cst.CodeValue as LearningStandardIdContentStandardName\n")
    .append("  FROM EdFi.edfi.CourseLearningStandard cls\n")
    .append("  LEFT JOIN EdFi.edfi.LearningStandard ls ON cls.LearningStandardId = ls.LearningStandardId\n")
    .append("  LEFT JOIN EdFi.edfi.ContentStandardType cst ON ls.ContentStandardTypeId = cst.ContentStandardTypeId\n")
    .append("  ORDER BY cls.EducationOrganizationId, cls.IdentityCourseCode\n")
    .toString();
    
    private void getData() {
        Connection conn = Utility.getConnection();
        this.coursesResultSet = Utility.getResultSet(conn, this.coursesQuery);
        this.courseCodesResultSet = Utility.getResultSet(conn, courseCodesQuery);
        this.courseLevelCharacteristicsResultSet = Utility.getResultSet(conn, courseLevelCharacteristicsQuery);
        this.gradeLevelsResultSet = Utility.getResultSet(conn, this.gradeLevelsQuery);
        this.learningStandardReferencesResultSet = Utility.getResultSet(conn, this.learningStandardReferencesQuery);
    }

    private void getCourse() {
        String course = Utility.generateXMLbasedOnTemplate(this.coursesResultSet, this.body, this.bodyKeyList);

        String tempCourseCodes = Utility.generateEmbeddedXMLbasedOnTemplate(this.coursesResultSet,
                this.courseCodesJoinKeys, this.courseCodesResultSet, this.courseCodes, this.courseCodesKeyList);
        course = Utility.replace(course, "--CourseCodes--\n", tempCourseCodes);

        String tempcourseLevelCharacteristics = Utility.generateEmbeddedXMLbasedOnTemplate(this.coursesResultSet,
                this.courseLevelCharacteristicsJoinKeys, this.courseLevelCharacteristicsResultSet,
                this.courseLevelCharacteristics, this.courseLevelCharacteristicsKeyList);
        course = Utility.replace(course, "--CourseLevelCharacteristics--\n", tempcourseLevelCharacteristics);

        String tempGradeLevels = Utility.generateEmbeddedXMLbasedOnTemplate(this.coursesResultSet,
                this.gradeLevelsJoinKeys, this.gradeLevelsResultSet, this.gradeLevels, this.gradeLevelsKeyList);
        course = Utility.replace(course, "--GradeLevels--\n", tempGradeLevels);

        String tempLearningStandardReferences = Utility.generateEmbeddedXMLbasedOnTemplate(this.coursesResultSet,
                this.learningStandardReferencesJoinKeys, this.learningStandardReferencesResultSet, 
                this.learningStandardReferences, this.learningStandardReferencesKeyList);
        course = Utility.replace(course, "--LearningStandardReferences--\n", tempLearningStandardReferences);

        System.out.println(course);
    }

    public void getCourses() {
        this.getData();

        System.out.print(this.begin);
        try {
            if (this.coursesResultSet != null) {
                do {
                    this.getCourse();
                } while (this.coursesResultSet.next());
            }
        } catch (SQLException e) {

        }

        System.out.print(this.end);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        (new GenerateCourse()).getCourses();
    }
}
