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
package org.slc.sli.api.selectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.selectors.doc.SelectorDocument;
import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryEngine;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.service.query.ApiQuery;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DefaultLogicalEntityTest {

    @Mock
    private ModelProvider provider;

    @Mock
    private SelectorSemanticModel selectorSemanticModel;

    @Mock
    private SelectorQueryEngine selectorQueryEngine;

    @Mock
    private SelectorDocument selectorDocument;

    @Mock
    private EntityDefinitionStore entityDefinitionStore;

    @Mock
    private ResourceHelper resourceHelper;

    @Mock
    private ApiQuery apiQuery;

    @InjectMocks
    private LogicalEntity logicalEntity = new DefaultLogicalEntity();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateEntities() {
        final EntityDefinition mockEntityDefinition = mock(EntityDefinition.class);
        when(mockEntityDefinition.getType()).thenReturn("TEST");
        when(resourceHelper.getEntityDefinition(anyString())).thenReturn(mockEntityDefinition);

        @SuppressWarnings("unchecked")
        final SelectorQuery mockPlan = mock(SelectorQuery.class);
        when(selectorQueryEngine.assembleQueryPlan(any(SemanticSelector.class))).thenReturn(mockPlan);

        final ApiQuery apiQuery = mock(ApiQuery.class);
        when(apiQuery.getSelector()).thenReturn(new HashMap<String, Object>());

        @SuppressWarnings("unchecked")
        final List<EntityBody> mockEntityList = mock(List.class);
        when(selectorDocument.aggregate(mockPlan, apiQuery)).thenReturn(mockEntityList);

        final List<EntityBody> entityList = logicalEntity.getEntities(apiQuery, "TEST");

        assertEquals(mockEntityList, entityList);
    }
}
