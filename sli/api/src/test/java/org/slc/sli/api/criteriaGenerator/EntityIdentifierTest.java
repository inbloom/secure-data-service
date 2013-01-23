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

package org.slc.sli.api.criteriaGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 1/17/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityIdentifierTest {
    @InjectMocks
    private EntityIdentifier entityIdentifier = new EntityIdentifier();

    @Mock
    private ModelProvider modelProvider;

    @Mock
    private EntityDefinitionStore entityDefinitionStore;

    private static final String PROGRAM = "program";

    private static final String SESSION = "session";

        private static final String STAFF_PROGRAM_ASSOC = "staffProgramAssociation";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindEntityWithBeginDate(){
        String request = "/educationOrganizations/id/sessions";
        EntityDefinition definition = mock(EntityDefinition.class);
        ClassType sessionClassType = mock(ClassType.class);
        Attribute attribute = mock(Attribute.class);
        Mockito.when(entityDefinitionStore.lookupByResourceName(anyString())).thenReturn(definition);
        Mockito.when(definition.getType()).thenReturn(SESSION);

        Mockito.when(modelProvider.getClassType(anyString())).thenReturn(sessionClassType);
        Mockito.when(sessionClassType.getBeginDateAttribute()).thenReturn(attribute);
        Mockito.when(attribute.getName()).thenReturn("beginDate");

        EntityFilterInfo entityFilterInfo = entityIdentifier.findEntity(request);
        assertFalse(entityFilterInfo.getBeginDateAttribute().isEmpty());

        //assertEquals("sessionId", entityFilterInfo.getSessionAttribute());


    }

    @Test
    public void testFindEntityWithAssociatedEntity() {
        String request = "/staff/id/staffProgramAssociation/programs";
        ClassType programClass = mock(ClassType.class);
        EntityDefinition definition = mock(EntityDefinition.class);
        Attribute attribute = mock(Attribute.class);
        ClassType staffProgramAssociation = mock(ClassType.class);

        List<String> programAssocEntity = new ArrayList<String>();
        programAssocEntity.add("staffProgramAssociation");
        programAssocEntity.add("studentProgramAssociation");
        Mockito.when(entityDefinitionStore.lookupByResourceName(anyString())).thenReturn(definition);
        Mockito.when(definition.getType()).thenReturn(PROGRAM);
        Mockito.when(modelProvider.getClassType(anyString())).thenReturn(programClass);
        Mockito.when(modelProvider.getClassType(STAFF_PROGRAM_ASSOC)).thenReturn(staffProgramAssociation);
        Mockito.when(programClass.getEndDateAttribute()).thenReturn(null);
        Mockito.when(programClass.getBeginDateAttribute()).thenReturn(null);
        Mockito.when(staffProgramAssociation.getEndDateAttribute()).thenReturn(attribute);
        Mockito.when(staffProgramAssociation.getBeginDateAttribute()).thenReturn(attribute);
        Mockito.when(staffProgramAssociation.getName()).thenReturn("StaffProgramAssociation");
        Mockito.when(modelProvider.getAssociatedDatedEntities(any(ClassType.class))).thenReturn(programAssocEntity);
        Mockito.when(modelProvider.getAssociationEnds(any(Identifier.class))).thenReturn(new ArrayList<AssociationEnd>());
        Mockito.when(attribute.getName()).thenReturn("beginDate");
        Mockito.when(modelProvider.getFilterBeginDateOn(any(ClassType.class))).thenReturn("");
        Mockito.when(modelProvider.getFilterEndDateOn(any(ClassType.class))).thenReturn("");

        EntityFilterInfo entityFilterInfo = entityIdentifier.findEntity(request);
        assertEquals("staffProgramAssociation", entityFilterInfo.getEntityName());

    }
}
