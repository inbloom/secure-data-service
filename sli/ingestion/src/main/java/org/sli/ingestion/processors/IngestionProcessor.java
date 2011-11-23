package org.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.enums.SexType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sli.ingestion.NeutralRecord;
import org.sli.ingestion.StudentSchoolAssociationInterchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;


/**
 * Ingestion Processor.
 * 
 * Base Camel Ingestion Processor which provides common ingestion behavior.
 * 
 */

@Component
public abstract class IngestionProcessor implements Processor {

    Logger log = LoggerFactory.getLogger(IngestionProcessor.class);
    
    public static final String SLI_DOMAIN_PACKAGE = "org.slc.sli.domain";

    @Autowired
    private ContextManager contextManager;
    
    private ObjectMapper jsonMapper;
    
    public IngestionProcessor() {
        super();
                
        // Setup Jackson JSON mapper
        this.jsonMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void init() {
    }
    
    public ContextManager getContextManager() {
        return this.contextManager;
    }    
    
    public ObjectMapper getJsonMapper() {
        return this.jsonMapper;
    }
    
    /**
     * Camel Exchange process callback method
     * 
     * @param exchange
     */
    @Override
    public void process(Exchange exchange) throws IOException, SAXException {
                
        // Indicate Camel processing
        log.info("processing: {}", exchange.getIn().getHeaders().toString());

        // Extract Ingestion input file
        File ingestionInputFile = exchange.getIn().getBody(File.class);
        
        // Setup Ingestion processor output file
        File ingestionOutputFile = File.createTempFile("camel_", ".tmp");
        ingestionOutputFile.deleteOnExit();
        
        // Allow Ingestion processor to process Camel exchange file
        this.processIngestionStream(ingestionInputFile, ingestionOutputFile);
        
        // Update Camel Exchange processor output result
        exchange.getOut().setBody(ingestionOutputFile);        
        
    }

    public abstract void processIngestionStream(File inputFile, File outputFile) throws IOException, SAXException;

    /**
     * Converts the SLI Ingestion JSON representation to a SLI Domain instance
     * 
     * @param json
     * @return
     */
    public Object mapFromJson(String json) throws IOException, SAXException {
        Object instance = null;
        
        // Extract the Ingestion components from the Ingestion file record/line using the JSON mapper
        ArrayList<Object> ingestionComponents = (ArrayList<Object>) this.getJsonMapper().readValue(json, Object.class);
        
        // Extract Ingestion metadata (operation, object class, etc.)
        String changeType = (String) ingestionComponents.get(0);
        String objectClassName = (String) ingestionComponents.get(1);
        
        // Extract Ingestion instance JSON representation
        Map<String, String> objectInstanceData = (Map<String, String>) ingestionComponents.get(2);
        
        try {
            
            // Verify Ingestion object class
            Class objectClass = Class.forName(objectClassName);
            
               log.debug(changeType + ": " + objectInstanceData);
               
            // Instantiate Ingestion instance from JSON
            instance = this.getJsonMapper().convertValue(objectInstanceData, objectClass);
                            
        } catch (ClassNotFoundException classNotFoundException) {
            log.error("invalid ingestion class type: " + objectClassName);
        }
        
        return instance;
    }

    /**
     * Converts the SLI Ingestion instance to a SLI Ingestion JSON representation
     * 
     * @param instance
     * @param operation
     * @return
     */
    public String mapToJson(Object instance, String operation) throws IOException, SAXException {
        String json = "";
        
        // Setup Ingestion record components
        Object[] ingestionComponents = new Object[3];

        // Ingestion metadata
        ingestionComponents[0] = operation;
        ingestionComponents[1] = instance.getClass().getName();
        
        // Ingestion instance data
        ingestionComponents[2] = instance;
        
        // Serialize Ingestion instance as JSON
        json = this.getJsonMapper().writeValueAsString(ingestionComponents);
        
        return json;
    }

    public Object mapFromNeutralRecord(String json) throws IOException, SAXException {
        
        // Extract the Ingestion neutral record instance from the Ingestion file record/line using the JSON mapper
        NeutralRecord neutralRecord = this.getJsonMapper().readValue(json, NeutralRecord.class);
        
        return this.mapFromNeutralRecord(neutralRecord);
    }
    
    /**
     * Converts the SLI Ingestion neutral record to a SLI Domain instance
     * 
     * @param neutralRecord
     * @return
     */
    public Object mapFromNeutralRecord(NeutralRecord neutralRecord) {
        Object instance = null;
        
        // Extract Ingestion metadata (operation, object class, etc.)
        String recordType = (String) neutralRecord.getRecordType();
        
        if (recordType.equals("Student")) {    

            Student student =  new Student();
            
            String studentIdString = neutralRecord.getAttributes().get("studentId");
            if (studentIdString != null) {
                Integer studentId = Integer.parseInt(studentIdString);
                student.setStudentId(studentId);
            }
            
            student.setStudentSchoolId(neutralRecord.getLocalId());
            student.setFirstName(neutralRecord.getAttributes().get("firstName"));
            student.setMiddleName(neutralRecord.getAttributes().get("middleName"));
            student.setLastSurname(neutralRecord.getAttributes().get("lastSurname"));
            String sex = neutralRecord.getAttributes().get("sex");
            if ((sex != null) && (sex.equals("Male"))) {
                student.setSex(SexType.Male);
                
            } else if ((sex != null) && (sex.equals("Female"))) {
                student.setSex(SexType.Female);
            }
            String birthDate = neutralRecord.getAttributes().get("birthDate");
            if (birthDate != null) {
                student.setBirthDate(java.sql.Date.valueOf(birthDate));
            }
            
            instance = student;
            
        } else if (recordType.equals("School")) {
            
            School school = new School();
            
            String schoolIdString = neutralRecord.getAttributes().get("schoolId");
            if (schoolIdString != null) {
                Integer schoolId = Integer.parseInt(schoolIdString);
                school.setSchoolId(schoolId);
            }
            
            school.setStateOrganizationId(neutralRecord.getLocalId());
            school.setFullName(neutralRecord.getAttributes().get("fullName"));
            
            instance = school;
            
        } else if (recordType.equals("StudentSchoolAssociation")) {
            
            StudentSchoolAssociationInterchange studentSchoolAssociation = new StudentSchoolAssociationInterchange();
            
            String associationIdString = neutralRecord.getLocalId();
            if (associationIdString != null) {
                Integer associationId = Integer.parseInt(associationIdString);
                studentSchoolAssociation.setAssociationId(associationId);
            }
            
            studentSchoolAssociation.setStudentUniqueStateId(neutralRecord.getLocalParentIds().get("Student"));
            studentSchoolAssociation.setStateOrganizationId(neutralRecord.getLocalParentIds().get("School"));
            
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
    public NeutralRecord mapToNeutralRecord(Object instance) {        
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
    private Object mapFromNeutralRecordUsingJackson(NeutralRecord neutralRecord) throws IOException, SAXException {
        Object instance = null;
        
        // Extract Ingestion metadata (operation, object class, etc.)
        String objectClassName = (String) neutralRecord.getRecordType();
        
        if (!objectClassName.contains(".")) {
            objectClassName = SLI_DOMAIN_PACKAGE + "." + objectClassName;
        }
        
        try {
            
            // Verify Ingestion object class
            Class objectClass = Class.forName(objectClassName);
            
            String objectJson = this.mapNeutralRecordToJson(neutralRecord);
            
            // Instantiate Ingestion instance from JSON
            instance = this.getJsonMapper().convertValue(objectJson, objectClass);
                            
        } catch (ClassNotFoundException classNotFoundException) {
            log.error("invalid ingestion class type: " + objectClassName);
        }
        
        return instance;
    }
    
    private String mapNeutralRecordToJson(NeutralRecord neutralRecord) {
        String json = "";
        
        if (neutralRecord.getRecordType().equals("Student")) {    
            json = this.mapNeutralRecordToJson(neutralRecord, "studentSchoolId", null);
            
        } else if (neutralRecord.getRecordType().equals("School")) {
            json = this.mapNeutralRecordToJson(neutralRecord, "stateOrganizationId", null);
            
        } else if (neutralRecord.getRecordType().equals("StudentSchoolAssociation")) {
            Map associationIdentifiers = new HashMap();
            
            neutralRecord.setRecordType("org.sli.ingestion.StudentSchoolAssociationInterchange");
            
            associationIdentifiers.put("Student", "studentSchoolId");
            associationIdentifiers.put("School", "stateOrganizationId");
            
            json = this.mapNeutralRecordToJson(neutralRecord, "associationId", associationIdentifiers);
        }
        
        return json;
    }
    
    private String mapNeutralRecordToJson(NeutralRecord neutralRecord, String coreIdentifier, Map<String, String> associationIdentifiers) {
        String json = "";
        
        json += "{";
        
        json += "\"" + coreIdentifier + "\"" + ": " + "\"" + neutralRecord.getLocalId() + "\"";
        
        if (associationIdentifiers != null) {
            Iterator idIterator = neutralRecord.getLocalParentIds().keySet().iterator();
            while (idIterator.hasNext()) {
                String id = (String) idIterator.next();
                
                String idAttributeValue = neutralRecord.getLocalParentIds().get(id);
                String idAttributeName = associationIdentifiers.get(id);
                
                json += "," + "\"" + idAttributeName + "\"" + ": " + "\"" + idAttributeValue + "\"";            
            }
        }
        
        json += "}";
        
        return json;
    }
    
}
