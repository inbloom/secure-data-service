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


package org.slc.sli.sample.transform;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * this class validate generated xml based on Ed-Fi schema
 *
 */
public class SchemaValidator {

    private static final String SCHEMA_DIR = "../../sli/domain/src/main/resources/edfiXsd-SLI/";

    private static final String SCHEMAS[] = {
            "SLI-Interchange-AssessmentMetadata.xsd",
            "SLI-Interchange-EducationOrgCalendar.xsd",
            "SLI-Interchange-EducationOrganization.xsd",
            "SLI-Interchange-HSGeneratedStudentTranscript.xsd",
            "SLI-Interchange-MasterSchedule.xsd",
            "SLI-Interchange-StaffAssociation.xsd",
            "SLI-Interchange-Student.xsd",
            "SLI-Interchange-StudentAssessment.xsd",
            "SLI-Interchange-StudentAttendance.xsd",
            "SLI-Interchange-StudentCohort.xsd",
            "SLI-Interchange-StudentDiscipline.xsd",
            "SLI-Interchange-StudentEnrollment.xsd",
            "SLI-Interchange-StudentGrade.xsd",
            "SLI-Interchange-StudentParent.xsd",
            "SLI-Interchange-StudentProgram.xsd" };

    /**
     * validate xml file in xmlDir folder.
     * @param xmlDir
     * @return
     * @throws Exception
     */
    public static String check(String xmlDir) throws Exception {

        Map<String, String> schemaMap = new HashMap<String, String>();
        for (String schema : SCHEMAS) {
            String schemaBase = schema.replace("Interchange", "").replace("-", "").replace("_", "").replace(".xsd", "");
            schemaMap.put(schemaBase, SCHEMA_DIR + schema);
        }

        for (File file : new File(xmlDir).listFiles()) {
            if (file.isFile()) {
                String fname = file.getName();
                String baseName = "";
                for (String key : schemaMap.keySet()) {
                    if (fname.contains(key) && fname.contains("xml")) {
                        baseName = key;
                    }
                }
                if (schemaMap.get(baseName) != null) {
                    String schemaFile = schemaMap.get(baseName);

                    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                    File schemaLocation = new File(schemaFile);
                    Schema schema = factory.newSchema(schemaLocation);
                    Validator validator = schema.newValidator();
                    Source source = new StreamSource(file);

                    try {
                        validator.validate(source);
                        System.out.println(file.getCanonicalPath() + " is valid. [" + schemaFile + "]");
                        System.out.println("");
                    } catch (SAXException ex) {
                        System.out.println("** ERROR **" + file.getCanonicalPath() + " is not valid. [" + schemaFile
                                + "]");
                        System.out.println(ex.getMessage());
                        System.out.println("");
                    }
                }
            }
        }
        return null;
    }
}
