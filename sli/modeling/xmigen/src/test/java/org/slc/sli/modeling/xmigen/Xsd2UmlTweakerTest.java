package org.slc.sli.modeling.xmigen;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.modeling.uml.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** 
* Xsd2UmlTweaker Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 11, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlTweakerTest {

    private static final String DATATYPE_NAME = "dataTypeName";
    private static final Identifier DATATYPE_ID = Identifier.random();
    private static final String CLASSTYPE_NAME = "classTypeName";
    private static final Identifier CLASSTYPE_ID = Identifier.random();
    private static final String UMLPACKAGE_NAME = "umlPackageName";

    Model model;

    @Mock
    Xsd2UmlPlugin plugin;

@Before
public void before() throws Exception {
    List<NamespaceOwnedElement> packageElements = new ArrayList<NamespaceOwnedElement>();
    List<NamespaceOwnedElement> modelElements = new ArrayList<NamespaceOwnedElement>();

    ClassType classType = new ClassType(CLASSTYPE_ID, CLASSTYPE_NAME, true, new ArrayList<Attribute>(0), new ArrayList<TaggedValue>(0));
    packageElements.add(classType);
    DataType dataType = new DataType(DATATYPE_ID, DATATYPE_NAME);
    packageElements.add(dataType);
    UmlPackage umlPackage = new UmlPackage(UMLPACKAGE_NAME, packageElements);
    modelElements.add(umlPackage);
    model = new Model(CLASSTYPE_ID, "modelName", new ArrayList<TaggedValue>(0), modelElements);
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: tweak(final Model model, final Xsd2UmlPlugin plugin) 
* 
*/ 
@Test
public void testTweak() throws Exception {
   Model newModel = Xsd2UmlTweaker.tweak(model,plugin);
    assertNotNull(newModel);
}
@Test
public void testInstance() throws Exception {
    Xsd2UmlTweaker tweaker = new Xsd2UmlTweaker();
    assertNotNull(tweaker);
}


} 
