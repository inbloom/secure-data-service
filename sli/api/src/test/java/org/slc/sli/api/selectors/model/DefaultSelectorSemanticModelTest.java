package org.slc.sli.api.selectors.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorSemanticModelTest {

    @Autowired
    private DefaultSelectorSemanticModel defaultSelectorSemanticModel;
    private ModelProvider provider;

    final static String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);
    }

    @Test
    public void testSemanticParser() throws SelectorParseException {
        final Type student = provider.getClassType("Student");
        final SemanticSelector selector;

        selector = defaultSelectorSemanticModel.parse(generateSelectorObjectMap(), student);

        assertTrue("Should contain base type", selector.containsKey(student));
        assertNotNull("Should have a list of attributes", selector.get(student));

        final List<SelectorElement> studentList = selector.get(student);
        assertEquals(5, studentList.size());
        assertTrue(!studentList.contains(null));
    }

    @Test(expected = SelectorParseException.class)
    public void testInvalidSelectors() throws SelectorParseException {
        final Type student = provider.getClassType("Student");
        final SemanticSelector selector =
                defaultSelectorSemanticModel.parse(generateFaultySelectorObjectMap(), student);
    }

    public Map<String, Object> generateFaultySelectorObjectMap() {
        final Map<String, Object> studentAttrs = generateSelectorObjectMap();
        studentAttrs.put("someNonExistentObject", true);
        return studentAttrs;
    }

    public Map<String, Object> generateSelectorObjectMap() {
        Map<String, Object> schoolAttrs = new HashMap<String, Object>();
        schoolAttrs.put("schoolType", true);

        Map<String, Object> ssaAttrs = new HashMap<String, Object>();
        ssaAttrs.put("entryGradeLevel", true);
        ssaAttrs.put("entryDate", true);
        ssaAttrs.put("school", schoolAttrs);

        Map<String, Object> studentsAttrs = new HashMap<String, Object>();
        studentsAttrs.put("name", true);
        studentsAttrs.put("economicDisadvantaged", true);
        studentsAttrs.put("sectionAssociations", true);
        studentsAttrs.put("schoolAssociations", ssaAttrs);
        studentsAttrs.put("sections", "*");

        return studentsAttrs;
    }
}
