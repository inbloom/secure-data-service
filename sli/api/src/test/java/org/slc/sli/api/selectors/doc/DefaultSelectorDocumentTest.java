package org.slc.sli.api.selectors.doc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.selectors.model.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorElement;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

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
        ClassType studentType = provider.getClassType("Student");
        ClassType studentSchoolAssocication = provider.getClassType("schoolAssociations<=>student");
        ClassType studentSectionAssocication = provider.getClassType("sectionAssociations<=>student");
        ModelElement entryGradeLevel = mock(ModelElement.class);
        ModelElement entryDate = mock(ModelElement.class);
        ModelElement someField = mock(ModelElement.class);
        ModelElement name = mock(ModelElement.class);
        ModelElement economicDisadvantaged = mock(ModelElement.class);

        SemanticSelector ssaAttrs = new SemanticSelector();
        List<SelectorElement> attributes = new ArrayList<SelectorElement>();
        attributes.add(new BooleanSelectorElement(entryGradeLevel, true));
        attributes.add(new BooleanSelectorElement(entryDate, true));
        ssaAttrs.put(studentSchoolAssocication, attributes);


        SemanticSelector sectionAttrs = new SemanticSelector();
        List<SelectorElement> attributes2 = new ArrayList<SelectorElement>();
        attributes2.add(new BooleanSelectorElement(someField, true));
        sectionAttrs.put(studentSectionAssocication, attributes2);


        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, true));
        attributes1.add(new BooleanSelectorElement(economicDisadvantaged, true));
        attributes1.add(new ComplexSelectorElement(studentSchoolAssocication, ssaAttrs));
        attributes1.add(new ComplexSelectorElement(studentSectionAssocication, sectionAttrs));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

}
