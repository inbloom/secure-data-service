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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Tests for ContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })

public class WriteValidatorTest {

    public static final String ED_ORG_A = "edOrg-A";
    public static final String ED_ORG_B = "edOrg-B";
    public static final String SECTION_ID = "section-id";
    public static final String UN_ASSOCIATED_ED_ORG = "unAssociatedEdOrg";
    @Autowired
    private WriteValidator writeValidator;

    private UriInfo uriInfo;
    private SLIPrincipal principal;
    private PagingRepositoryDelegate<Entity> repo;
    private List<PathSegment> postPath;
    private List<PathSegment> putPath;
    private Entity existingSection;


    @SuppressWarnings("unchecked")
	@Before
    public void setUp() {
        uriInfo = Mockito.mock(UriInfo.class);
        principal = Mockito.mock(SLIPrincipal.class);
        repo = Mockito.mock(PagingRepositoryDelegate.class);

        PathSegment v1Path = Mockito.mock(PathSegment.class);
        when(v1Path.getPath()).thenReturn("v1");
        PathSegment sectionPath = Mockito.mock(PathSegment.class);
        when(sectionPath.getPath()).thenReturn(ResourceNames.SECTIONS);
        PathSegment idPath = Mockito.mock(PathSegment.class);
        when(idPath.getPath()).thenReturn(SECTION_ID);

        postPath = Arrays.asList(v1Path, sectionPath);
        putPath = Arrays.asList(v1Path, sectionPath, idPath);

        EntityBody entityBody = new EntityBody();
        existingSection = new MongoEntity(EntityNames.SECTION, SECTION_ID, entityBody, null);
        when(repo.findById(EntityNames.SECTION, SECTION_ID)).thenReturn(existingSection);
        writeValidator.setRepo(repo);

        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList(ED_ORG_A, ED_ORG_B));
    }

    @Test
    @ExpectedException(value = AccessDeniedException.class)
    public void testDenyWritingOutsideOfEdOrgHierarchyCreate() {
    	
    	EntityBody entityBody = new EntityBody();
    	entityBody.put(ParameterConstants.SCHOOL_ID, UN_ASSOCIATED_ED_ORG);

        when(uriInfo.getPathSegments()).thenReturn(postPath);

        writeValidator.validateWriteRequest(entityBody, uriInfo, principal);
        Assert.fail("should fail validation");
    }

    @Test
    public void testValidWritingInEdOrgHierarchyCreate() {

        EntityBody entityBody = new EntityBody();
    	entityBody.put(ParameterConstants.SCHOOL_ID, ED_ORG_B);

        when(uriInfo.getPathSegments()).thenReturn(postPath);

        writeValidator.validateWriteRequest(entityBody, uriInfo, principal);
        // should pass validation
    }
    
    @Test
    @ExpectedException(value = AccessDeniedException.class)
    public void testDenyWritingForEdOrgInEntityBodyUpdate() throws Exception {
        EntityBody entityBody = new EntityBody();
    	entityBody.put(ParameterConstants.SCHOOL_ID, ED_ORG_A);

        existingSection.getBody().put(ParameterConstants.SCHOOL_ID, UN_ASSOCIATED_ED_ORG);

        when(uriInfo.getPathSegments()).thenReturn(putPath);

        writeValidator.validateWriteRequest(entityBody, uriInfo, principal);
        Assert.fail("should fail validation");
    }
/*
    @Test
    public void testDenyWritingForEdOrgInPathUpdate() throws Exception {
    	Map<String, Object> newEntityBody = new HashMap<String, Object>();
    	newEntityBody.put(ParameterConstants.SCHOOL_ID, "school-a");
    	Entity newEntity = new MongoEntity(EntityNames.SECTION, newEntityBody);
    	PathSegment v1Path = Mockito.mock(PathSegment.class);
    	when(v1Path.getPath()).thenReturn("v1");
    	PathSegment sectionPath = Mockito.mock(PathSegment.class);
    	when(sectionPath.getPath()).thenReturn(ResourceNames.SECTIONS);
    	PathSegment idPath = Mockito.mock(PathSegment.class);
    	when(idPath.getPath()).thenReturn("section-id");
    	
    	Map<String, Object> existingEntityBody = new HashMap<String, Object>();
    	existingEntityBody.put(ParameterConstants.SCHOOL_ID, "school-d");
    	Entity existingEntity = new MongoEntity(EntityNames.SECTION, "section-id", existingEntityBody, null);
    	
    	when(uriInfo.getEntity(Entity.class)).thenReturn(newEntity);
        when(uriInfo.getMethod()).thenReturn("PUT");
        when(uriInfo.getPathSegments()).thenReturn(Arrays.asList(v1Path, sectionPath, idPath));
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("school-a", "school-b", "school-c"));
        when(repo.findById(EntityNames.SECTION, "section-id")).thenReturn(existingEntity);
        
        writeValidator.setRepo(repo);
        Method validateEdOrgWrite = writeValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", ContainerRequest.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(writeValidator, new Object[]{uriInfo, principal});

        Assert.assertFalse("should fail validation", isValid.booleanValue());
    }

    @Test
    public void testValidWritingInEdOrgHierarchyUpdate() throws Exception {
    	Map<String, Object> newEntityBody = new HashMap<String, Object>();
    	newEntityBody.put(ParameterConstants.SCHOOL_ID, "school-a");
    	Entity newEntity = new MongoEntity(EntityNames.SECTION, newEntityBody);
    	PathSegment v1Path = Mockito.mock(PathSegment.class);
    	when(v1Path.getPath()).thenReturn("v1");
    	PathSegment sectionPath = Mockito.mock(PathSegment.class);
    	when(sectionPath.getPath()).thenReturn(ResourceNames.SECTIONS);
    	PathSegment idPath = Mockito.mock(PathSegment.class);
    	when(idPath.getPath()).thenReturn("section-id");
    	
    	Map<String, Object> existingEntityBody = new HashMap<String, Object>();
    	existingEntityBody.put(ParameterConstants.SCHOOL_ID, "school-b");
    	Entity existingEntity = new MongoEntity(EntityNames.SECTION, "section-id", existingEntityBody, null);
    	
    	when(uriInfo.getEntity(Entity.class)).thenReturn(newEntity);
        when(uriInfo.getMethod()).thenReturn("PUT");
        when(uriInfo.getPathSegments()).thenReturn(Arrays.asList(v1Path, sectionPath, idPath));
        when(principal.getSubEdOrgHierarchy()).thenReturn(Arrays.asList("school-a", "school-b", "school-c"));
        when(repo.findById(EntityNames.SECTION, "section-id")).thenReturn(existingEntity);
        
        writeValidator.setRepo(repo);
        Method validateEdOrgWrite = writeValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", ContainerRequest.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(writeValidator, new Object[]{uriInfo, principal});

        Assert.assertTrue("should pass validation", isValid.booleanValue());
    	
    }
*/

}
