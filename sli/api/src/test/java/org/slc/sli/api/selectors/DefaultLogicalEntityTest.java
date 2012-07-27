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
import org.slc.sli.api.selectors.doc.Constraint;
import org.slc.sli.api.selectors.doc.SelectorDocument;
import org.slc.sli.api.selectors.doc.SelectorQueryEngine;
import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.Type;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        when(entityDefinitionStore.lookupByResourceName(anyString())).thenReturn(mockEntityDefinition);
        @SuppressWarnings("unchecked")
        final Map<Type, SelectorQueryPlan> mockPlan = mock(Map.class);
        when(selectorQueryEngine.assembleQueryPlan(any(SemanticSelector.class))).thenReturn(mockPlan);

        final Constraint mockConstraint = mock(Constraint.class);
        @SuppressWarnings("unchecked")
        final List<EntityBody> mockEntityList = mock(List.class);
        when(selectorDocument.aggregate(mockPlan, mockConstraint)).thenReturn(mockEntityList);

        final List<EntityBody> entityList =
                logicalEntity.createEntities(new HashMap<String, Object>(), mockConstraint, "TEST");

        assertEquals(mockEntityList, entityList);
    }
}