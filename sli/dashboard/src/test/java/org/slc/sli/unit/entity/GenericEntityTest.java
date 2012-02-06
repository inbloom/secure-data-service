package org.slc.sli.unit.entity;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.entity.GenericEntityOld;

/**
 * Unit tests for the GenericEntity class
 * 
 */
public class GenericEntityTest {

    @Before
    public void setup() {
        
    }

    @Test
    public void testFromJson() {
 
    	// given a json representation of student
    	String studentJson = "{\"otherName\":[],\"sex\":\"Female\",\"studentIndicators\":[],\"studentCharacteristics\":[],\"hispanicLatinoEthnicity\":false,\"disabilities\":[],\"cohortYears\":[],\"homeLanguages\":[],\"section504Disabilities\":{\"section504Disability\":[\"Other\"]},\"learningStyles\":null,\"links\":[{\"rel\":\"self\",\"href\":\"https://devapp1.slidev.org/api/rest/students/00000000-0000-0000-0000-000000000001\"},{\"rel\":\"getStudentSchoolAssociations\",\"href\":\"https://devapp1.slidev.org/api/rest/student-school-associations/00000000-0000-0000-0000-000000000001\"},{\"rel\":\"getSchools\",\"href\":\"https://devapp1.slidev.org/api/rest/student-school-associations/00000000-0000-0000-0000-000000000001/targets\"},{\"rel\":\"getStudentAssessmentAssociations\",\"href\":\"https://devapp1.slidev.org/api/rest/student-assessment-associations/00000000-0000-0000-0000-000000000001\"},{\"rel\":\"getAssessments\",\"href\":\"https://devapp1.slidev.org/api/rest/student-assessment-associations/00000000-0000-0000-0000-000000000001/targets\"},{\"rel\":\"getStudentSectionAssociations\",\"href\":\"https://devapp1.slidev.org/api/rest/student-section-associations/00000000-0000-0000-0000-000000000001\"},{\"rel\":\"getSections\",\"href\":\"https://devapp1.slidev.org/api/rest/student-section-associations/00000000-0000-0000-0000-000000000001/targets\"}],\"limitedEnglishProficiency\":\"Yes\",\"race\":[\"White\"],\"studentIdentificationCode\":[],\"programParticipations\":[],\"id\":\"00000000-0000-0000-0000-000000000001\",\"languages\":[],\"studentUniqueStateId\":900000001,\"address\":[{\"streetNumberName\":\"146 Alder Cresent\",\"postalCode\":\"68000\",\"stateAbbreviation\":\"IL\",\"countryCode\":\"US\",\"city\":\"Chicago\"}],\"electronicMail\":[{\"emailAddressType\":\"Home/Personal\",\"emailAddress\":\"msmith@test.slidev.org\"}],\"name\":{\"verification\":\"Birth certificate\",\"lastSurname\":\"SMITH\",\"firstName\":\"MARY\"},\"schoolFoodServicesEligibility\":\"Free\",\"telephone\":[{\"telephoneNumberType\":\"Home\",\"primaryTelephoneNumberIndicator\":false,\"telephoneNumber\":\"(123) 456-7890\"}],\"birthData\":{\"stateOfBirthAbbreviation\":\"IL\",\"countryOfBirthCode\":\"US\",\"birthDate\":\"1999-01-01\",\"cityOfBirth\":\"Addison\"}}";
    	
    	// convert to generic entity
    	Gson gson = new Gson();
    	Map<String, Object> map = gson.fromJson(studentJson, new TypeToken<Map<String, Object>>(){ }.getType());
    	GenericEntityOld entity = new GenericEntityOld();
    	entity.setType("student");
    	entity.setEntityId("01");
    	entity.setBody(map);
    	
    	// check entity fields are correct
    	System.out.println(entity.getBody().get("sex"));
    	System.out.println(entity.getBody().get("birthData"));	
    	System.out.println(((Map)(entity.getBody().get("birthData"))).get("cityOfBirth"));
    	System.out.println(entity.getBody().get("links"));
    	System.out.println(((List)(entity.getBody().get("links"))).get(0));
    	System.out.println(entity.getBody());
    }
    
    @Test
    public void testFromJsonArray() {
    	
    	String studentJson = "[{\"studentUniqueStateId\": \"525878920\", \"name\": {\"middleName\": \"D\", \"lastSurname\": \"Sims\", \"firstName\": \"Delilah\"}, \"economicDisadvantaged\": true, \"birthData\": {\"birthDate\": \"2004-12-04\"}, \"sex\": \"Female\", \"limitedEnglishProficiency\":\"No\",\"schoolFoodServiceEligibility\":\"None\",\"id\": \"525878920\"}, {\"studentUniqueStateId\": \"525878921\", \"name\": {\"middleName\": \"D\", \"lastSurname\": \"Sims2\", \"firstName\": \"Delilah\"}, \"economicDisadvantaged\": true, \"birthData\": {\"birthDate\": \"2004-12-04\"}, \"sex\": \"Female\", \"limitedEnglishProficiency\":\"No\",\"schoolFoodServiceEligibility\":\"None\",\"id\": \"525878921\"}]";
    	Gson gson = new Gson();
    	List<Map<String, Object>> maps = gson.fromJson(studentJson, new TypeToken<List<Map<String, Object>>>(){ }.getType());
    	
    	GenericEntityOld entity = new GenericEntityOld();
    	entity.setType("student");
    	entity.setEntityId("01");
    	entity.setBody(maps.get(0));
    	System.out.println(entity.getBody());
    	
    	GenericEntityOld entity2 = new GenericEntityOld();
    	entity2.setType("student");
    	entity2.setEntityId("02");
    	entity2.setBody(maps.get(1));
    	System.out.println(entity2.getBody());
    }   	
}