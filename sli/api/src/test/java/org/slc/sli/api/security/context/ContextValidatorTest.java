/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.security.context;

import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.PathSegment;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Tests for ContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class ContextValidatorTest {

    @Autowired
    private ContextValidator contextValidator;

    private ContainerRequest containerRequest;
    private SLIPrincipal principal;
    private PagingRepositoryDelegate<Entity> repo;

    @SuppressWarnings("unchecked")
	@Before
    public void setUp() {
        containerRequest = Mockito.mock(ContainerRequest.class);
        principal = Mockito.mock(SLIPrincipal.class);
        repo = Mockito.mock(PagingRepositoryDelegate.class);
    }

    @Test
    public void testDenyWritingOutsideOfEdOrgHierarchyCreate() throws Exception {
    	
    	Map<String, Object> entityBody = new HashMap<String, Object>();
    	entityBody.put(ParameterConstants.SCHOOL_ID, "unshared-id");
    	Entity entity = new MongoEntity(EntityNames.SECTION, entityBody);
    	
    	when(containerRequest.getEntity(Entity.class)).thenReturn(entity);
        when(containerRequest.getMethod()).thenReturn("POST");
        when(containerRequest.getPathSegments()).thenReturn(Arrays.asList(new PathSegment[]{}));
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("valid-id", "second-valid-id"));

        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", Entity.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{entity, principal});

        Assert.assertFalse("should fail validation", isValid.booleanValue());

    }

    @Test
    public void testValidWritingInEdOrgHierarchyCreate() throws Exception {
    	Map<String, Object> entityBody = new HashMap<String, Object>();
    	entityBody.put(ParameterConstants.SCHOOL_ID, "1234existsOnEntity");
    	Entity entity = new MongoEntity(EntityNames.SECTION, entityBody);
    	
    	when(containerRequest.getEntity(Entity.class)).thenReturn(entity);

        when(containerRequest.getMethod()).thenReturn("POST");
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("1234existsOnEntity"));
        //TODO setup inputs to have a matching ed org

        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", Entity.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{entity, principal});

        Assert.assertTrue("should pass validation", isValid.booleanValue());

    }
    
    
    @Test
    public void testDenyWritingForEdOrgInEntityBodyUpdate() throws Exception {
    	Map<String, Object> newEntityBody = new HashMap<String, Object>();
    	newEntityBody.put(ParameterConstants.SCHOOL_ID, "valid-path-id");
    	Entity newEntity = new MongoEntity(EntityNames.SECTION, newEntityBody);
    	PathSegment sectionPath = Mockito.mock(PathSegment.class);
    	when(sectionPath.getPath()).thenReturn(ResourceNames.SECTIONS);
    	PathSegment idPath = Mockito.mock(PathSegment.class);
    	when(idPath.getPath()).thenReturn("valid-path-id");
    	
    	Map<String, Object> existingEntityBody = new HashMap<String, Object>();
    	existingEntityBody.put(ParameterConstants.SCHOOL_ID, "invalid-body-id");
    	Entity existingEntity = new MongoEntity(EntityNames.SECTION, existingEntityBody);
    	
    	when(containerRequest.getEntity(Entity.class)).thenReturn(newEntity);
        when(containerRequest.getMethod()).thenReturn("PUT");
        when(containerRequest.getPathSegments()).thenReturn(Arrays.asList(sectionPath, idPath));
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("valid-path-id", "valid-id", "second-valid-id"));
        when(repo.findById(EntityNames.SECTION, "section-id")).thenReturn(existingEntity);
        
        contextValidator.setRepo(repo);
        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", Entity.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{newEntity, principal});

        Assert.assertFalse("should fail validation", isValid.booleanValue());
    }
    
    @Test
    public void testDenyWritingForEdOrgInPathUpdate() throws Exception {
    	Map<String, Object> newEntityBody = new HashMap<String, Object>();
    	newEntityBody.put(ParameterConstants.SCHOOL_ID, "invalid-path-id");
    	Entity newEntity = new MongoEntity(EntityNames.SECTION, newEntityBody);
    	PathSegment sectionPath = Mockito.mock(PathSegment.class);
    	when(sectionPath.getPath()).thenReturn(ResourceNames.SECTIONS);
    	PathSegment idPath = Mockito.mock(PathSegment.class);
    	when(idPath.getPath()).thenReturn("valid-path-id");
    	
    	Map<String, Object> existingEntityBody = new HashMap<String, Object>();
    	existingEntityBody.put(ParameterConstants.SCHOOL_ID, "valid-body-id");
    	Entity existingEntity = new MongoEntity(EntityNames.SECTION, existingEntityBody);
    	
    	when(containerRequest.getEntity(Entity.class)).thenReturn(newEntity);
        when(containerRequest.getMethod()).thenReturn("PUT");
        when(containerRequest.getPathSegments()).thenReturn(Arrays.asList(sectionPath, idPath));
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("valid-body-id", "valid-id", "second-valid-id"));
        when(repo.findById(EntityNames.SECTION, "section-id")).thenReturn(existingEntity);
        
        contextValidator.setRepo(repo);
        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", Entity.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{newEntity, principal});

        Assert.assertFalse("should fail validation", isValid.booleanValue());

    }

    @Test
    public void testValidWritingInEdOrgHierarchyUpdate() throws Exception {
        when(containerRequest.getMethod()).thenReturn("PUT");
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("1234existsOnEntity"));
        //TODO setup inputs to have a matching ed org

        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", ContainerRequest.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{containerRequest, principal});

        Assert.assertTrue("should pass validation", isValid.booleanValue());
    	
    }


}
