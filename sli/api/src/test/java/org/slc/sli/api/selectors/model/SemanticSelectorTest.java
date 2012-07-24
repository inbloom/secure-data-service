package org.slc.sli.api.selectors.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.modeling.uml.Type;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SemanticSelectorTest {
    private SemanticSelector selector;

    @Before
    public void setup() {
        selector = new SemanticSelector();
    }

    @Test
    public void testAddSelector() {
        final Type testType = mock(Type.class);
        final SemanticSelector embeddedSelector = new SemanticSelector();
        selector.addSelector(testType, embeddedSelector);
        assertNotNull(selector.get(testType));
        assertEquals(1, selector.get(testType).size());

        selector.addSelector(testType, "attribute");
        assertEquals(2, selector.get(testType).size());
    }
}
