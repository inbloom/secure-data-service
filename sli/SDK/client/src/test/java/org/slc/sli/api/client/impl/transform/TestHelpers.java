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


package org.slc.sli.api.client.impl.transform;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.client.impl.BasicLink;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Helper methods and constants for unit tests.
 */
public class TestHelpers {
    
    static ObjectMapper mapper = new ObjectMapper();
    
    static JsonNode initJsonNode(final String json) {
        
        JsonNode rval = null;
        try {
            rval = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            rval = null;
        } catch (IOException e) {
            rval = null;
        }
        
        return rval;
    }
    
    /**
     * Helper functions.
     */
    public static GenericEntity createSimpleGenericEntity() {
        GenericEntity rval = null;
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("String", "StringValue");
        body.put("Integer", Integer.parseInt("1"));
        body.put("Long", Long.parseLong("2"));
        body.put("Double", Double.parseDouble("4.0"));
        body.put("Boolean", Boolean.FALSE);
        rval = new GenericEntity("GenericType", body);
        
        return rval;
    }
    
    public static GenericEntity createComplexEntity() {
        GenericEntity rval = null;
        
        Map<String, Object> body = new HashMap<String, Object>();
        
        body.put("loginId", "a");
        body.put("otherName", new LinkedList<Map<String, Object>>());
        body.put("sex", "Male");
        body.put("staffUniqueStateId", "mjohnson");
        body.put("hispanicLatinoEthnicity", false);
        body.put("yearsOfPriorTeachingExperience", 0);
        body.put("yearsOfPriorProfessionalExperience", 20);
        
        List<Map<String, Object>> addresses = new LinkedList<Map<String, Object>>();
        Map<String, Object> address1 = new HashMap<String, Object>();
        address1.put("apartmentRoomSuiteNumber", "7B");
        address1.put("streetNumberName", "123 Wall Street");
        address1.put("postalCode", "99999");
        address1.put("stateAbbreviation", "IL");
        address1.put("countryCode", "SI");
        address1.put("addressType", "Work");
        address1.put("city", "Chicago");
        addresses.add(address1);
        
        body.put("address", addresses);
        
        Map<String, Object> name = new HashMap<String, Object>();
        name.put("verification", "Life insurance policy");
        name.put("lastSurname", "Johnson");
        name.put("personalTitlePrefix", "Mr");
        name.put("firstName", "Michael");
        body.put("name", name);
        
        List<Map<String, Object>> email = new LinkedList<Map<String, Object>>();
        Map<String, Object> email1 = new HashMap<String, Object>();
        email1.put("emailAddressType", "Organization");
        email1.put("emailAddress", "junk@junk.com");
        email.add(email1);
        body.put("electronicMail", email);
        
        body.put("highestLevelOfEducationCompleted", "No Degree");
        
        List<Map<String, Object>> credentials = new LinkedList<Map<String, Object>>();
        Map<String, Object> credential = new HashMap<String, Object>();
        
        List<Map<String, Object>> credentialFields = new LinkedList<Map<String, Object>>();
        Map<String, Object> credentialField1 = new HashMap<String, Object>();
        credentialField1.put("description", "Linux Superstar");
        Map<String, Object> credentialField2 = new HashMap<String, Object>();
        credentialField2.put("codeValue", "IT Admin");
        credentialFields.add(credentialField1);
        credentialFields.add(credentialField2);
        credential.put("credentialField", credentialFields);
        credential.put("level", "All Level (Grade Level PK-12)");
        credential.put("teachingCredentialType", "Standard");
        credential.put("credentialType", "Certification");
        credential.put("credentialIssuanceDate", "2000-01-01");
        credentials.add(credential);
        
        body.put("credentials", credentials);
        body.put("birthDate", "1980-02-01");
        
        List<Map<String, Object>> phoneNumbers = new LinkedList<Map<String, Object>>();
        Map<String, Object> phoneNumber = new HashMap<String, Object>();
        phoneNumber.put("telephoneNumberType", "Fax");
        phoneNumber.put("primaryTelephoneNumberIndicator", true);
        phoneNumber.put("telephoneNumber", "a");
        phoneNumbers.add(phoneNumber);
        
        body.put("telephone", phoneNumbers);
        
        List<Map<String, Object>> staffCodes = new LinkedList<Map<String, Object>>();
        Map<String, Object> staffCode = new HashMap<String, Object>();
        staffCode.put("identificationSystem", "Selective Service");
        staffCode.put("ID", "a");
        staffCode.put("assigningOrganizationCode", "a");
        staffCodes.add(staffCode);
        
        body.put("staffIdentificationCode", staffCodes);
        
        rval = new GenericEntity("staff", body);
        return rval;
    }
    
    public static BasicLink createBasicLink() {
        try {
            return new BasicLink("test", new URL("http://www.test.com"));
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    public static final String SIMPLE_JSON_BODY = "{\"Double\":4.0,\"Long\":2,"
            + "\"String\":\"StringValue\",\"Boolean\":false,\"Integer\":1}";
    
    public static final String SIMPLE_JSON = "{\"entityType\":\"GenericType\",\"body\":" + SIMPLE_JSON_BODY + "}";
    
    public static final JsonNode SIMPLE_JSON_OBJECT = initJsonNode(SIMPLE_JSON);
    
    public static final String SIMPLE_METADATA_JSON = "{\"entityType\":\"GenericType\",\"body\":" + SIMPLE_JSON_BODY
            + ",\"metaData\":{\"tenantId\":\"IL\"," + "\"externalId\":\"linda.kim\"}}";
    
    public static final JsonNode SIMPLE_METADATA_JSON_OBJECT = initJsonNode(SIMPLE_METADATA_JSON);
    
    public static final String COMPLEX_JSON_BODY = "{\"loginId\":\"a\",\"otherName\":[],"
            + "\"sex\":\"Male\",\"staffUniqueStateId\":\"mjohnson\",\"hispanicLatinoEthnicity\":false,"
            + "\"yearsOfPriorTeachingExperience\":0,\"yearsOfPriorProfessionalExperience\":20,\"address\":"
            + "[{\"apartmentRoomSuiteNumber\":\"7B\",\"postalCode\":\"99999\","
            + "\"streetNumberName\":\"123 Wall Street\",\"stateAbbreviation\":\"IL\",\"countryCode\":\"SI\","
            + "\"addressType\":\"Work\",\"city\":\"Chicago\"}],\"name\":{"
            + "\"verification\":\"Life insurance policy\",\"lastSurname\":\"Johnson\","
            + "\"personalTitlePrefix\":\"Mr\",\"firstName\":\"Michael\"},\"electronicMail\":["
            + "{\"emailAddress\":\"junk@junk.com\",\"emailAddressType\":\"Organization\"}],"
            + "\"highestLevelOfEducationCompleted\":\"No Degree\",\"credentials\":[{"
            + "\"credentialField\":[{\"description\":\"Linux Superstar\"},{\"codeValue\":\"IT Admin\"}"
            + "],\"level\":\"All Level (Grade Level PK-12)\",\"teachingCredentialType\":\"Standard\","
            + "\"credentialType\":\"Certification\",\"credentialIssuanceDate\":\"2000-01-01\"}],"
            + "\"birthDate\":\"1980-02-01\",\"telephone\":[{\"telephoneNumber\":\"a\","
            + "\"primaryTelephoneNumberIndicator\":true,\"telephoneNumberType\":\"Fax\"}],"
            + "\"staffIdentificationCode\":[{\"identificationSystem\":\"Selective Service\","
            + "\"ID\":\"a\",\"assigningOrganizationCode\":\"a\"}]}";
    
    public static final String COMPLEX_JSON = "{\"entityType\":\"staff\",\"body\":" + COMPLEX_JSON_BODY
            + ",\"metaData\":{\"tenantId\":\"IL\",\"externalId\":\"mjohnson\"}}";
    
    public static final JsonNode COMPLEX_JSON_OBJECT = initJsonNode(COMPLEX_JSON);
    
    public static final String LINK_JSON = "{\"rel\":\"test\",\"href\":\"http://www.test.com\"}";
    
    public static final JsonNode LINK_JSON_OBJECT = initJsonNode(LINK_JSON);
    
    @SuppressWarnings("unchecked")
    public static boolean basicEntitiesEqual(GenericEntity e, GenericEntity r) {
        
        if (!e.getEntityType().equals(r.getEntityType())) {
            return false;
        }
        
        Map<String, Object> eData = e.getData();
        Map<String, Object> rData = r.getData();
        
        // only compare body elements
        if (rData.containsKey(GenericEntitySerializer.ENTITY_BODY_KEY)) {
            rData = (Map<String, Object>) rData.get(GenericEntitySerializer.ENTITY_BODY_KEY);
        }
        
        if (eData.size() != rData.size()) {
            return false;
        }
        
        for (Map.Entry<String, Object> entry : eData.entrySet()) {
            if (!rData.containsKey(entry.getKey())) {
                return false;
            }
            
            Object eObj = entry.getValue();
            Object rObj = rData.get(entry.getKey());
            
            if (eObj instanceof Map) {
                return compareMap(eObj, rObj);
            } else if (eObj instanceof List) {
                return compareList(eObj, rObj);
            } else {
                return eObj.toString().equals(rObj.toString());
            }
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private static boolean compareList(Object eObj, Object rObj) {
        if (eObj == null || rObj == null) {
            return false;
        }
        if (!(rObj instanceof List)) {
            return false;
        }
        
        List<Object> eList = (List<Object>) eObj;
        List<Object> rList = (List<Object>) rObj;
        
        if (eList.size() != rList.size()) {
            return false;
        }
        
        if (!(eList.containsAll(rList) && rList.containsAll(eList))) {
            return false;
        }
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private static boolean compareMap(Object eObj, Object rObj) {
        if (eObj == null || rObj == null) {
            return false;
        }
        if (!(rObj instanceof Map)) {
            return false;
        }
        
        Map<String, Object> eMap = (Map<String, Object>) eObj;
        Map<String, Object> rMap = (Map<String, Object>) rObj;
        
        if (eMap.size() != rMap.size()) {
            return false;
        }
        
        Set<Map.Entry<String, Object>> eMapEntries = eMap.entrySet();
        Set<Map.Entry<String, Object>> rMapEntries = rMap.entrySet();
        
        if (!(eMapEntries.containsAll(rMapEntries) && rMapEntries.containsAll(eMapEntries))) {
            return false;
        }
        
        return true;
    }
}
