package org.slc.sli.api.selectors.doc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.selectors.model.*;
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
import static org.mockito.Mockito.when;

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
    public void testComplexSelector() {
        SemanticSelector selectorsWithType =  generateSelectorObjectMap();

        List<String> ids = new ArrayList<String>();
        ids.add("1234");
        Constraint constraint = new Constraint();
        constraint.setKey("id");
        constraint.setValue(ids);

        defaultSelectorDocument.aggregate(selectorsWithType, constraint);
    }

    @Test
    public void testIncludeAllSelector() {
        SemanticSelector selectorsWithType =  generateIncludeAllSelectorObjectMap();

        List<String> ids = new ArrayList<String>();
        ids.add("1234");
        Constraint constraint = new Constraint();
        constraint.setKey("id");
        constraint.setValue(ids);

        defaultSelectorDocument.aggregate(selectorsWithType, constraint);
    }

    public SemanticSelector generateIncludeAllSelectorObjectMap() {
        ClassType studentType = provider.getClassType("Student");
        ClassType studentSchoolAssocicationType = provider.getClassType("schoolAssociations<=>student");

        ModelElement name = mock(ModelElement.class);
        ModelElement economicDisadvantaged = mock(ModelElement.class);

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, true));
        attributes1.add(new BooleanSelectorElement(economicDisadvantaged, true));
        attributes1.add(new IncludeAllSelectorElement(studentSchoolAssocicationType));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

    public SemanticSelector generateSelectorObjectMap() {
        ClassType sectionType = provider.getClassType("Section");
        ClassType studentType = provider.getClassType("Student");
        ClassType studentSchoolAssocicationType = provider.getClassType("schoolAssociations<=>student");
        ClassType studentSectionAssocicationType = provider.getClassType("sectionAssociations<=>student");

        ModelElement entryGradeLevel = mock(ModelElement.class);
        ModelElement entryDate = mock(ModelElement.class);
        ModelElement someField = mock(ModelElement.class);
        ModelElement sessionId = mock(ModelElement.class);
        ClassType name = mock(ClassType.class);
        when(name.getName()).thenReturn("name");

        ModelElement economicDisadvantaged = mock(ModelElement.class);

        SemanticSelector studentSchoolAttrs = new SemanticSelector();
        List<SelectorElement> attributes = new ArrayList<SelectorElement>();
        attributes.add(new BooleanSelectorElement(entryGradeLevel, true));
        attributes.add(new BooleanSelectorElement(entryDate, true));
        studentSchoolAttrs.put(studentSchoolAssocicationType, attributes);

        SemanticSelector sectionAttrs = new SemanticSelector();
        List<SelectorElement> attributes3 = new ArrayList<SelectorElement>();
        attributes3.add(new BooleanSelectorElement(sessionId, true));
        sectionAttrs.put(sectionType, attributes3);

        SemanticSelector studentSectionAttrs = new SemanticSelector();
        List<SelectorElement> attributes2 = new ArrayList<SelectorElement>();
        attributes2.add(new ComplexSelectorElement(sectionType, sectionAttrs));
        studentSectionAttrs.put(studentSectionAssocicationType, attributes2);
        attributes2.add(new BooleanSelectorElement(someField, true));
        sectionAttrs.put(studentSectionAssocicationType, attributes2);

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, true));
        attributes1.add(new BooleanSelectorElement(economicDisadvantaged, true));
        attributes1.add(new ComplexSelectorElement(studentSchoolAssocicationType, studentSchoolAttrs));
        attributes1.add(new ComplexSelectorElement(studentSectionAssocicationType, studentSectionAttrs));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

}
