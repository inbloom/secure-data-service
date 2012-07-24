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
    public void testSemanticParser() {
        final Type type = provider.getClassType("Student");

        Map<Type, Object> selectorsWithType = defaultSelectorSemanticModel.parse(generateSelectorObjectMap(), type);

        assertTrue("Should have type", selectorsWithType.containsKey(type));
        assertTrue("Should be a list", selectorsWithType.get(type) instanceof List);
        assertEquals("Should have 3 elements", ((List<Object>) selectorsWithType.get(type)).size(), 3);
    }

    public Map<String, Object> generateSelectorObjectMap() {
        //Map<String, Object> schoolAttrs = new HashMap<String, Object>();
        //schoolAttrs.put("nameOfInstitution", true);

        Map<String, Object> ssaAttrs = new HashMap<String, Object>();
        ssaAttrs.put("entryGradeLevel", true);
        ssaAttrs.put("entryDate", true);
        //ssaAttrs.put("schoolId", schoolAttrs);

        Map<String, Object> studentsAttrs = new HashMap<String, Object>();
        studentsAttrs.put("name", true);
        studentsAttrs.put("economicDisadvantaged", true);
        //studentsAttrs.put("sectionAssociations", true);
        studentsAttrs.put("schoolAssociations", ssaAttrs);
        System.out.println(studentsAttrs);

        return studentsAttrs;
    }
}
