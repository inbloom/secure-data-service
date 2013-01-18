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
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Collections;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindEntity(){
        String request = "/educationOrganizations/id/sessions";
        EntityDefinition definition = mock(EntityDefinition.class);
        ClassType entityClassType = mock(ClassType.class);
        Attribute attribute = mock(Attribute.class);
        Mockito.when(entityDefinitionStore.lookupByResourceName(anyString())).thenReturn(definition);
        Mockito.when(definition.getType()).thenReturn("session");
        Mockito.when(modelProvider.getClassType(anyString())).thenReturn(entityClassType);
        Mockito.when(entityClassType.getBeginDateAttribute()).thenReturn(attribute);
        Mockito.when(attribute.getName()).thenReturn("beginDate");

        EntityFilterInfo entityFilterInfo = entityIdentifier.findEntity(request);
        assertFalse(entityFilterInfo.getBeginDateAttribute().isEmpty());

//       request = "/staff/id/staffProgramAssociation/programs";
//        entityFilterInfo = entityIdentifier.findEntity(request);
//        assertEquals("staffProgramAssociatons", entityFilterInfo.getEntityName().isEmpty());
//        assertEquals("sessionId", entityFilterInfo.getSessionAttribute());


    }
}
