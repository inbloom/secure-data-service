package org.slc.sli.test.utils;

import java.io.File;
import java.io.IOException;
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

    private static final String SCHEMA_DIR = "../../sli/domain/src/main/resources/edfiXsd/";

    private static final String SCHEMAS[] = {
            "Interchange-AssessmentMetadata.xsd",
            "Interchange-EducationOrgCalendar.xsd",
            "Interchange-EducationOrganization.xsd",
            "Interchange-HSGeneratedStudentTranscript.xsd",
            "Interchange-MasterSchedule.xsd",
            "Interchange-StaffAssociation.xsd",
            "Interchange-Student.xsd",
            "Interchange-StudentAssessment.xsd",
            "Interchange-StudentAttendance.xsd",
            "Interchange-StudentCohort.xsd",
            "Interchange-StudentDiscipline.xsd",
            "Interchange-StudentEnrollment.xsd",
            "Interchange-StudentGrade.xsd",
            "Interchange-StudentParent.xsd",
            "Interchange-StudentProgram.xsd" };

    public static String check(String xmlDir) throws Exception {

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
                if (schemaMap.get(baseName) != null) {
                    String schemaFile = schemaMap.get(baseName);

                    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
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
