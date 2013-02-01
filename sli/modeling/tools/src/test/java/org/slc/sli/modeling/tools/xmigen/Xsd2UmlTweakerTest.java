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

package org.slc.sli.modeling.tools.xmigen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slc.sli.modeling.uml.*;

import java.util.ArrayList;
import java.util.List;

//import org.slc.sli.modeling.uml.*;

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
    Xsd2UmlHostedPlugin plugin;

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



/** 
* 
* Method: tweak(final Model model, final Xsd2UmlPlugin plugin) 
* 
*/ 
@Test
public void testTweak() throws Exception {
    Model newModel = Xsd2UmlTweaker.tweak(model, plugin);
    Assert.assertNotNull(newModel);
}
@Test
public void testInstance() throws Exception {
    Xsd2UmlTweaker tweaker = new Xsd2UmlTweaker();
    Assert.assertNotNull(tweaker);
}


} 
