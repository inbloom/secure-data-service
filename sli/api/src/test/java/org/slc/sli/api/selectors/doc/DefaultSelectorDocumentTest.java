package org.slc.sli.api.selectors.doc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class DefaultSelectorDocumentTest {

    @Autowired
    DefaultSelectorDocument defaultSelectorDocument;

    private ModelProvider provider;

    final static String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);
    }

    @Test
    public void testQueryMap() {
        SemanticSelector selectorsWithType =  generateSelectorObjectMap();

        List<String> ids = new ArrayList<String>();
        ids.add("1234");
        Constraint constraint = new Constraint();
        constraint.setKey("id");
        constraint.setValue(ids);

        defaultSelectorDocument.aggregate(selectorsWithType, constraint);
    }

    public SemanticSelector generateSelectorObjectMap() {
        ClassType studentType =provider.getClassType("Student");
        ClassType studentSchoolAssocication = provider.getClassType("schoolAssociations<=>student");
        ClassType studentSectionAssocication = provider.getClassType("sectionAssociations<=>student");

        SemanticSelector ssaAttrs = new SemanticSelector();
        List<Object> attributes = new ArrayList<Object>();
        attributes.add("entryGradeLevel");
        attributes.add("entryDate");
        ssaAttrs.put(studentSchoolAssocication, attributes);


        SemanticSelector sectionAttrs = new SemanticSelector();
        List<Object> attributes2 = new ArrayList<Object>();
        attributes2.add("someField");
        sectionAttrs.put(studentSectionAssocication, attributes2);


        SemanticSelector studentsAttrs = new SemanticSelector();
        List<Object> attributes1 = new ArrayList<Object>();
        attributes1.add("name");
        attributes1.add("economicDisadvantaged");
        attributes1.add(ssaAttrs);
        attributes1.add(sectionAttrs);
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }
}
