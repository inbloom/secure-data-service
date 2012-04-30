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

    private static final String SCHEMA_DIR = "./edfiXsd/";

    private static final String SCHEMAS[] = {
            "Interchange-AssessmentMetadata.xsd",
            "Interchange-EducationOrgCalendar.xsd",
            "Interchange-EducationOrganization.xsd",
            "Interchange-HSGeneratedStudentTranscript.xsd",
            "Interchange-MasterSchedule.xsd",
            "Interchange-StaffAssociation.xsd",
            "Interchange-StudentAssessment.xsd",
            "Interchange-StudentAttendance.xsd",
            "Interchange-StudentCohort.xsd",
            "Interchange-StudentDiscipline.xsd",
            "Interchange-StudentEnrollment.xsd",
            "Interchange-StudentGrade.xsd",
            "Interchange-StudentParent.xsd",
            "Interchange-StudentProgram.xsd" };

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
                String baseName = fname.replace("Interchange", "").replace("-", "").replace("_", "")
                        .replace(".xml", "");
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
