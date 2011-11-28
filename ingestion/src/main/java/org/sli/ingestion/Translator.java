package org.sli.ingestion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import net.wgen.sli.domain.School;
import net.wgen.sli.domain.Student;
import net.wgen.sli.domain.enums.SexType;


/**
 * Utility class for representation conversions routines.  Used to translate among 
 * the various object representations including XML, JSON, AVRO Neutral Record.
 * 
 */

@Component
public class Translator {

    private static Logger log = LoggerFactory.getLogger(Translator.class);
    
    public static final String SLI_DOMAIN_PACKAGE = "net.wgen.sli.domain";
    private static ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Converts the SLI Ingestion instance to a SLI Ingestion JSON representation
     * 
     * @param instance
     * @param operation
     * @return
     */
    public static String mapToJson(Object instance, String operation) throws IOException, SAXException {
        String json = "";
        
        // Setup Ingestion record components
        Object[] ingestionComponents = new Object[3];

        // Ingestion metadata
        ingestionComponents[0] = operation;
        ingestionComponents[1] = instance.getClass().getName();
        
        // Ingestion instance data
        ingestionComponents[2] = instance;
        
        // Serialize Ingestion instance as JSON
        json = jsonMapper.writeValueAsString(ingestionComponents);
        
        return json;
    }

    public static Object mapFromNeutralRecord(String json) throws IOException, SAXException {
        
        // Extract the Ingestion neutral record instance from the Ingestion file record/line using the JSON mapper
        NeutralRecord neutralRecord = jsonMapper.readValue(json, NeutralRecord.class);
        
        return mapFromNeutralRecord(neutralRecord);
    }
    
    /**
     * Converts the SLI Ingestion neutral record to a SLI Domain instance
     * 
     * @param neutralRecord
     * @return
     */
    public static Object mapFromNeutralRecord(NeutralRecord neutralRecord) {
        Object instance = null;
        
        // Extract Ingestion metadata (operation, object class, etc.)
        String recordType = (String) neutralRecord.getRecordType();
        
        if (recordType.equals("Student")) {    

            Student student =  new Student();
            
            String studentIdString = (String) neutralRecord.getAttributes().get("studentId");
            if (studentIdString != null) {
                Integer studentId = Integer.parseInt(studentIdString);
                student.setStudentId(studentId);
            }
            
            student.setStudentSchoolId(neutralRecord.getLocalId().toString());
            student.setFirstName((String) neutralRecord.getAttributes().get("firstName"));
            student.setMiddleName((String) neutralRecord.getAttributes().get("middleName"));
            student.setLastSurname((String) neutralRecord.getAttributes().get("lastSurname"));
            String sex = (String) neutralRecord.getAttributes().get("sex");
            if ((sex != null) && (sex.equals("Male"))) {
                student.setSex(SexType.Male);
                
            } else if ((sex != null) && (sex.equals("Female"))) {
                student.setSex(SexType.Female);
            }
            String birthDate = (String) neutralRecord.getAttributes().get("birthDate");
            if (birthDate != null) {
                student.setBirthDate(java.sql.Date.valueOf(birthDate));
            }
            
            instance = student;
            
        } else if (recordType.equals("School")) {
            
            School school = new School();
            
            String schoolIdString = (String) neutralRecord.getAttributes().get("schoolId");
            if (schoolIdString != null) {
                Integer schoolId = Integer.parseInt(schoolIdString);
                school.setSchoolId(schoolId);
            }
            
            school.setStateOrganizationId(neutralRecord.getLocalId().toString());
            school.setFullName((String) neutralRecord.getAttributes().get("fullName"));
            
            instance = school;
            
        } else if (recordType.equals("StudentSchoolAssociation")) {
            
            StudentSchoolAssociationInterchange studentSchoolAssociation = new StudentSchoolAssociationInterchange();
            
            String associationIdString = neutralRecord.getLocalId().toString();
            if (associationIdString != null) {
                Integer associationId = Integer.parseInt(associationIdString);
                studentSchoolAssociation.setAssociationId(associationId);
            }
            
            studentSchoolAssociation.setStudentUniqueStateId((String) neutralRecord.getLocalParentIds().get("Student"));
            studentSchoolAssociation.setStateOrganizationId((String) neutralRecord.getLocalParentIds().get("School"));
            
            instance = studentSchoolAssociation;
        }
        
        return instance;
    }
    
    /**
     * Converts the SLI Domain instance to a SLI Ingestion neutral record
     * 
     * @param neutralRecord
     * @return
     */
    public static NeutralRecord mapToNeutralRecord(Object instance) {        
        NeutralRecord neutralRecord = new NeutralRecord();
        
        if (instance instanceof Student) {    
            Student student = (Student) instance;
            
            neutralRecord.setLocalId("" + student.getStudentSchoolId());
            neutralRecord.setRecordType("Student");
            neutralRecord.getAttributes().put("studentId", "" + student.getStudentId());
            neutralRecord.getAttributes().put("firstName", student.getFirstName());
            neutralRecord.getAttributes().put("middleName", student.getMiddleName());
            neutralRecord.getAttributes().put("lastSurname", student.getLastSurname());
            neutralRecord.getAttributes().put("sex", "" + student.getSex());
            neutralRecord.getAttributes().put("birthDate", "" + student.getBirthDate());
            
        } else if (instance instanceof School) {
            School school = (School) instance;

            neutralRecord.setLocalId("" + school.getStateOrganizationId());
            neutralRecord.setRecordType("School");
            neutralRecord.getAttributes().put("schoolId", "" + school.getSchoolId());
            neutralRecord.getAttributes().put("fullName", school.getFullName());
            
        } else if (instance instanceof StudentSchoolAssociationInterchange) {
            StudentSchoolAssociationInterchange studentSchoolAssociation = (StudentSchoolAssociationInterchange) instance;

            neutralRecord.setLocalId("" + studentSchoolAssociation.getAssociationId());
            neutralRecord.setRecordType("StudentSchoolAssociation");
            neutralRecord.getLocalParentIds().put("Student", studentSchoolAssociation.getStudentUniqueStateId());
            neutralRecord.getLocalParentIds().put("School", studentSchoolAssociation.getStateOrganizationId());
        }

        return neutralRecord;
    }

    /**
     * Future Use - Converts the SLI Ingestion neutral record to a SLI Domain instance by leveraging a JSON mapper
     * 
     * @param neutralRecord
     * @return
     */
    public static Object mapFromNeutralRecordUsingJackson(NeutralRecord neutralRecord) throws IOException, SAXException {
        Object instance = null;
        
        // Extract Ingestion metadata (operation, object class, etc.)
        String objectClassName = (String) neutralRecord.getRecordType();
        
        if (!objectClassName.contains(".")) {
            objectClassName = SLI_DOMAIN_PACKAGE + "." + objectClassName;
        }
        
        try {
            
            // Verify Ingestion object class
            Class objectClass = Class.forName(objectClassName);
            
            String objectJson = mapNeutralRecordToJson(neutralRecord);
            
            // Instantiate Ingestion instance from JSON
            instance = jsonMapper.convertValue(objectJson, objectClass);
                            
        } catch (ClassNotFoundException classNotFoundException) {
            log.error("invalid ingestion class type: " + objectClassName);
        }
        
        return instance;
    }
    
    private static String mapNeutralRecordToJson(NeutralRecord neutralRecord) {
        String json = "";
        
        if (neutralRecord.getRecordType().equals("Student")) {    
            json = mapNeutralRecordToJson(neutralRecord, "studentSchoolId", null);
            
        } else if (neutralRecord.getRecordType().equals("School")) {
            json = mapNeutralRecordToJson(neutralRecord, "stateOrganizationId", null);
            
        } else if (neutralRecord.getRecordType().equals("StudentSchoolAssociation")) {
            Map associationIdentifiers = new HashMap();
            
            neutralRecord.setRecordType("org.sli.ingestion.StudentSchoolAssociationInterchange");
            
            associationIdentifiers.put("Student", "studentSchoolId");
            associationIdentifiers.put("School", "stateOrganizationId");
            
            json = mapNeutralRecordToJson(neutralRecord, "associationId", associationIdentifiers);
        }
        
        return json;
    }
    
    private static String mapNeutralRecordToJson(NeutralRecord neutralRecord, String coreIdentifier, Map<String, String> associationIdentifiers) {
        String json = "";
        
        json += "{";
        
        json += "\"" + coreIdentifier + "\"" + ": " + "\"" + neutralRecord.getLocalId() + "\"";
        
        if (associationIdentifiers != null) {
            Iterator idIterator = neutralRecord.getLocalParentIds().keySet().iterator();
            while (idIterator.hasNext()) {
                String id = (String) idIterator.next();
                
                String idAttributeValue = (String) neutralRecord.getLocalParentIds().get(id);
                String idAttributeName = associationIdentifiers.get(id);
                
                json += "," + "\"" + idAttributeName + "\"" + ": " + "\"" + idAttributeValue + "\"";            
            }
        }
        
        json += "}";
        
        return json;
    }
    
}
