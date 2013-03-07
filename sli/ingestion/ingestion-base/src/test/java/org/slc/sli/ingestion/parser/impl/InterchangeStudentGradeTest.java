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
package org.slc.sli.ingestion.parser.impl;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author dduran
 *
 */
public class InterchangeStudentGradeTest {

    @Test
    public void testStudentAcademicRecord() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/StudentAcademicRecord.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/StudentAcademicRecord.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testCourseTranscript() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/CourseTranscript.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/CourseTranscript.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testReportCard() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/ReportCard.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/ReportCard.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testGrade() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/Grade.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/Grade.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentCompetency() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/StudentCompetency.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/StudentCompetency.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testDiploma() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/Diploma.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/Diploma.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testGradebookEntry() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/GradebookEntry.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/GradebookEntry.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentGradebookEntry() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/StudentGradebookEntry.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/StudentGradebookEntry.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testCompetencyLevelDescriptor() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/CompetencyLevelDescriptor.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/CompetencyLevelDescriptor.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testLearningObjective() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/LearningObjective.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/LearningObjective.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentCompetencyObjective() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentGrade.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentGrade/StudentCompetencyObjective.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentGrade/StudentCompetencyObjective.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

}
