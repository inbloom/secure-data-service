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


/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeConnection;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author rlatta
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class PathFindingContextResolverTest {
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private PathFindingContextResolver resolver;
    
    private AssociativeContextHelper mockHelper;
    
    private Repository<Entity> mockRepo;

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockHelper = Mockito.mock(AssociativeContextHelper.class);
        mockRepo = Mockito.mock(Repository.class);
        resolver.setHelper(mockHelper);
        resolver.setRepository(mockRepo);
        List<String> tsKeys = Arrays.asList(new String[] { "teacherId", "sectionId" });
        List<String> ssKeys = Arrays.asList(new String[] { "sectionId", "studentId" });
        when(mockHelper.getAssocKeys(eq(EntityNames.TEACHER), any(AssociationDefinition.class))).thenReturn(tsKeys);
        when(mockHelper.getAssocKeys(eq(EntityNames.SECTION), any(AssociationDefinition.class))).thenReturn(ssKeys);
    }

    @Test
    public void testCanResolve() throws Exception {
        assertFalse(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        assertFalse(resolver.canResolve(EntityNames.STUDENT, EntityNames.TEACHER));
        assertFalse(resolver.canResolve(EntityNames.AGGREGATION, EntityNames.TEACHER));
    }

    
    @Test
    public void testGetResourceName() throws Exception {
        String currentNodeType = "currentNodeType";
        String nextNodeType = "nextNodeType";
        String connectionNodeType = "connectionNodeType";
        
        SecurityNode mockCurrentNode = Mockito.mock(SecurityNode.class);
        SecurityNode mockNextNode = Mockito.mock(SecurityNode.class);
        SecurityNodeConnection mockConnection = Mockito.mock(SecurityNodeConnection.class);

        when(mockCurrentNode.getType()).thenReturn(currentNodeType);
        when(mockNextNode.getType()).thenReturn(nextNodeType);
        when(mockConnection.getAssociationNode()).thenReturn(connectionNodeType);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(connectionNodeType));
        when(mockConnection.getAssociationNode()).thenReturn("");
        when(mockConnection.isReferenceInSelf()).thenReturn(true);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(currentNodeType));
        when(mockConnection.isReferenceInSelf()).thenReturn(false);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(nextNodeType));
    }
}
