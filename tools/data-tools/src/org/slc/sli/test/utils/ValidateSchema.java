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


package org.slc.sli.test.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidateSchema {
    public static String SCHEMA_DIR;

    public static List<String> SCHEMAS = new ArrayList<String>();

    public static void getSchemaVersion(){
        if("sliXsd-R1".equalsIgnoreCase(org.slc.sli.test.xmlgen.StateEdFiXmlGenerator.XSDVersionPath)){
            SCHEMA_DIR = "../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/";
            
        } else {
            SCHEMA_DIR = "../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/";
        }
        if ("../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/".equalsIgnoreCase(SCHEMA_DIR)) {
            SCHEMAS.add("SLI-Interchange-AssessmentMetadata.xsd");
            SCHEMAS.add("SLI-Interchange-EducationOrgCalendar.xsd");
            SCHEMAS.add("SLI-Interchange-EducationOrganization.xsd");
            SCHEMAS.add("SLI-Interchange-HSGeneratedStudentTranscript.xsd");
            SCHEMAS.add("SLI-Interchange-MasterSchedule.xsd");
            SCHEMAS.add("SLI-Interchange-StaffAssociation.xsd");
            SCHEMAS.add("SLI-Interchange-Student.xsd");
            SCHEMAS.add("SLI-Interchange-StudentAssessment.xsd");
            SCHEMAS.add("SLI-Interchange-StudentAttendance.xsd");
            SCHEMAS.add("SLI-Interchange-StudentCohort.xsd");
            SCHEMAS.add("SLI-Interchange-StudentDiscipline.xsd");
            SCHEMAS.add("SLI-Interchange-StudentEnrollment.xsd");
            SCHEMAS.add("SLI-Interchange-StudentGrade.xsd");
            SCHEMAS.add("SLI-Interchange-StudentParent.xsd");
            SCHEMAS.add("SLI-Interchange-StudentProgram.xsd");
        } else {
            SCHEMAS.add("Interchange-Section.xsd");
        }
    }    

    public static String check(String xmlDir) throws Exception {
  
        org.slc.sli.test.xmlgen.StateEdFiXmlGenerator.XSDVersionPath = "sliXsd";
        getSchemaVersion();
        Map<String, String> schemaMap = new HashMap<String, String>();
        for (String schema : SCHEMAS) {
            String schemaBase = schema.replace("Interchange", "").replace("-", "").replace("_", "").replace(".xsd", "");
            schemaMap.put(schemaBase, SCHEMA_DIR + schema);

        }

        long totalErrorCount = 0;
        for (File file : new File(xmlDir).listFiles()) {
            if (file.isFile()) {
                String fname = file.getName();
                String baseName = fname.replace("Interchange", "").replace("-", "").replace("_", "")
                        .replace(".xml", "");
                
                if (schemaMap.get("SLI" + baseName) != null) {
                    String schemaFile = schemaMap.get("SLI" + baseName);
 
                    SchemaFactory factory;

                    factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
  
                    File schemaLocation = new File(schemaFile);

                    Schema schema = factory.newSchema(schemaLocation);

                    Validator validator = schema.newValidator();
                    Source source = new StreamSource(file);
                
                    final Map<String, List<Integer>> errorReport = new HashMap<String, List<Integer>>();
                    
                    try {

                            validator.setErrorHandler(new ErrorHandler() {

                            @Override
                            public void warning(SAXParseException exception) throws SAXException {
                                handle(exception);
                              
                            }

                            @Override
                            public void fatalError(SAXParseException exception) throws SAXException {
                                handle(exception);
                               
                            }

                            @Override
                            public void error(SAXParseException exception) throws SAXException {
                               
                                handle(exception);
                                
                            }
                                                        
                            private void handle(SAXParseException exception) {
                                String error = exception.getMessage();
                                if(!errorReport.containsKey(error)) { 
                                    errorReport.put(error, new LinkedList<Integer>());
                                }
                                errorReport.get(error).add(new Integer(exception.getLineNumber()));
                            }
                            
                        });
                        validator.validate(source);
                        totalErrorCount += errorReport.size(); 
                        if(errorReport.size() > 0) {                            
                            System.out.println("Errors for " + file.getCanonicalPath() + ". [" + schemaFile + "]");
                            for(String error: errorReport.keySet()) {
                                List<Integer> lines = errorReport.get(error);
                                System.out.println("Lines  "  + ((lines.size() > 5 )? (lines.subList(0, 4) + "..."):(lines)) + lines.size() + " errors. " + error);
                            }
                            System.out.println("");
                        }
                        else {
                            System.out.println(file.getCanonicalPath() + " is valid. [" + schemaFile + "]");
                            System.out.println("");
                        }
                                               
                    } catch (SAXException ex) {
                        System.out.println("** ERROR **" + file.getCanonicalPath() + " is not valid. [" + schemaFile
                                + "]");
                        System.out.println(ex.getMessage());
                        System.out.println("");
                    }
                }
            }
        }
        if(totalErrorCount > 0) 
            System.out.println("                                ******************************************" + totalErrorCount + " ERRORS" + "**************************************************");
        return null;
    }


                    
    
    public static void main(String[] args) throws SAXException, IOException, Exception {
        ValidateSchema.check("./data/");
    }

}
