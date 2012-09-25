package org.slc.sli.modeling.xmigen;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/** 
* Xsd2UmlLookup Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 11, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlLookupTest { 
    private Xsd2UmlLookup<ClassType> testObject;
@Before
public void before() throws Exception {
   testObject = new Xsd2UmlLookup<ClassType>();
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: from(final T key) 
* 
*/ 
@Test
public void testFrom() throws Exception {
    ClassType classType =  mock(ClassType.class);
    Identifier id = testObject.from(classType);
    assertNotNull(id);
    Identifier dupId = testObject.from(classType);
    assertEquals(id,dupId);
}

@Test(expected = NullPointerException.class)
public void testFormQualifiedName() throws Exception {
    testObject.from(null);
}

} 
