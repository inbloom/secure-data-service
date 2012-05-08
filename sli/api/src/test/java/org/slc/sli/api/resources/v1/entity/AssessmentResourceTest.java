package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import org.slc.sli.api.config.EntityDefinitionStore;

/**
 * JUnit for assessment resources
 * 
 * @author nbrown
 * 
 */
public class AssessmentResourceTest {
    
    private EntityDefinitionStore defs = mock(EntityDefinitionStore.class);
    private AssessmentResource resource = new AssessmentResource(defs);
    
    @Test
    public void testGetLearningStandards() {
        Response response = resource.getLearningStandards("any");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testGetLearningObjectives() {
        Response response = resource.getLearningObjectives("any");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
}
