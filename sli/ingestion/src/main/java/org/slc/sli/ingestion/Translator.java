package org.slc.sli.ingestion;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;


/**
 * Utility class for representation conversions routines.  Used to translate among
 * the various object representations including XML, JSON, AVRO Neutral Record.
 *
 */

@Component
public class Translator {

    private static Logger log = LoggerFactory.getLogger(Translator.class);

    public static final String SLI_DOMAIN_PACKAGE = "org.slc.sli.domain";
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

    public static Entity mapFromNeutralRecord(String json) throws IOException, SAXException {

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
    public static Entity mapFromNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.putAll(neutralRecord.getAttributes());

        return new MongoEntity(neutralRecord.getRecordType(), null, Collections.unmodifiableMap(body), null);
    }

    /**
     * Converts the SLI Domain instance to a SLI Ingestion neutral record
     *
     * @param neutralRecord
     * @return
     */
    public static NeutralRecord mapToNeutralRecord(Entity instance) {
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType(instance.getType());

        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> body = instance.getBody();

        for (String key : body.keySet()) {
            attributes.put(key, body.get(key));
        }

        neutralRecord.setAttributes(attributes);

        return neutralRecord;
    }

    /**
     * Future Use - Converts the SLI Ingestion neutral record to a SLI Domain instance by leveraging a JSON mapper
     *
     * @param neutralRecord
     * @return
     */
    @Deprecated
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

            neutralRecord.setRecordType("org.slc.sli.ingestion.StudentSchoolAssociationInterchange");

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
