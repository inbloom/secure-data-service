package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

public class StudentAssessmentConverterTest {

	@InjectMocks
	StudentAssessmentConverter assessmentConverter = new StudentAssessmentConverter();
	
    @Mock
    INaturalKeyExtractor naturalKeyExtractor; 
    
    @Before
    public void setup() {
    	naturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
    	MockitoAnnotations.initMocks(this);
    }
    
	/*
	 * subdocs in embedded data
	 */
    private Entity createUpConvertEntity() {
        String entityType = "studentAssessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        List<Entity> embedded = new ArrayList<Entity>();
        Map<String, Object> item2 = new HashMap<String, Object>();
        item2.put("studentAssessmentId", "ID");
        item2.put("_id", "IDChildID");
        item2.put("abc", "somevalue");
        embedded.add(new MongoEntity("studentAssessmentItem", null, item2, null));
        embeddedData.put("studentAssessmentItem", embedded);

        return new MongoEntity(entityType, entityId, body, metaData, null, null, embeddedData, null);
    }
    
    @Test
    public void upconvertNoAssessmentShouldRemoveInvalidReference() {
    	List<Entity> entity = Arrays.asList(createUpConvertEntity());
    	assertNotNull(entity.get(0).getEmbeddedData().get("studentAssessmentItem"));
    	assessmentConverter.subdocToBodyField(entity);
    	assertNull(entity.get(0).getBody().get("studentAssessmentItem"));
    }
    
}
