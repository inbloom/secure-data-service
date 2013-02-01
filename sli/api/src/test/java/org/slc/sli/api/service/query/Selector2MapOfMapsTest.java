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

package org.slc.sli.api.service.query;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.slc.sli.api.selectors.model.SelectorParseException;

/**
 * Tests the ability to parse a String into a map of maps representing a selector. Contains both
 * success and error checking.
 *
 *
 *
 * @author kmyers
 *
 */
public class Selector2MapOfMapsTest {

    private SelectionConverter selectionConverter = new Selector2MapOfMaps();

    @Test(expected = SelectorParseException.class)
    public void testDollarSignThrowsExceptionWhenNotExpected() {
        this.selectionConverter.convert(":($)");
    }

    @Test(expected = SelectorParseException.class)
    public void testNestedDollarSignThrowsExceptionWhenNotExpected() {
        this.selectionConverter.convert(":(foo:($))");
    }


    @Test
    public void testDollarSignDoesNotThrowExceptionWhenExpected() {
        SelectionConverter mySelectionConverter = new Selector2MapOfMaps(false);
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("$", true);

        Map<String, Object> convertResult = mySelectionConverter.convert(":($)");

        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }


    @Test
    public void testBasicWildcard() throws SelectorParseException {
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("*", true);
        Map<String, Object> convertResult = this.selectionConverter.convert(":( * )".replaceAll(" ", ""));

        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }

    @Test
    public void testBasicString() throws SelectorParseException {
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("name", true);
        expectedResult.put("sectionAssociations", true);
        Map<String, Object> convertResult = this.selectionConverter.convert(":( name, sectionAssociations )".replaceAll(" ", ""));

        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }

    @Test
    public void testTwiceNestedString() throws SelectorParseException {
        Map<String, Object> convertResult = this.selectionConverter.convert(":( name, sectionAssociations : ( studentId , sectionId : ( * ) ) )".replaceAll(" ", ""));

        Map<String, Object> sectionIdMap = new HashMap<String, Object>();
        sectionIdMap.put("*", true);

        Map<String, Object> sectionAssociationsMap = new HashMap<String, Object>();
        sectionAssociationsMap.put("studentId", true);
        sectionAssociationsMap.put("sectionId", sectionIdMap);

        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("name", true);
        expectedResult.put("sectionAssociations", sectionAssociationsMap);

        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }

    @Test
    public void testExcludingFeaturesFromWildcardSelection() throws SelectorParseException {
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("*", true);
        expectedResult.put("sequenceOfCourse", false);
        Map<String, Object> convertResult = this.selectionConverter.convert(":( *, sequenceOfCourse:false )".replaceAll(" ", ""));

        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }

    @Test
    public void veryNestedTest() throws SelectorParseException {
        String selectorString = ":(foo:(bar),foo2:(bar2:true),foo3:(bar3:false),foo4:(bar4:(*,foobar5:false)))";
        Map<String, Object> fooMap = new HashMap<String, Object>();
        fooMap.put("bar", true);
        Map<String, Object> foo2Map = new HashMap<String, Object>();
        foo2Map.put("bar2", true);
        Map<String, Object> foo3Map = new HashMap<String, Object>();
        foo3Map.put("bar3", false);
        Map<String, Object> foo4Map = new HashMap<String, Object>();
        Map<String, Object> bar4Map = new HashMap<String, Object>();
        bar4Map.put("*", true);
        bar4Map.put("foobar5", false);
        foo4Map.put("bar4", bar4Map);
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("foo", fooMap);
        expectedResult.put("foo2", foo2Map);
        expectedResult.put("foo3", foo3Map);
        expectedResult.put("foo4", foo4Map);

        Map<String, Object> convertResult = this.selectionConverter.convert(selectorString);


        assertTrue(convertResult != null);
        assertTrue(convertResult.equals(expectedResult));
    }

    @Test(expected = SelectorParseException.class)
    public void testInvalidSyntax() throws SelectorParseException {
        this.selectionConverter.convert(":(");
    }

    @Test(expected = SelectorParseException.class)
    public void testEmptyStrings() throws SelectorParseException {
        this.selectionConverter.convert(":(,,)");
    }

    @Test(expected = SelectorParseException.class)
    public void testUnbalancedParens() throws SelectorParseException {
        Selector2MapOfMaps.getMatchingClosingParenIndex("((", 0);
    }

    @Test(expected = SelectorParseException.class)
    public void testUnbalancedParens2() throws SelectorParseException {
        Selector2MapOfMaps.getMatchingClosingParenIndex(")", 0);
    }

    @Test(expected = SelectorParseException.class)
    public void testUnbalancedParens3() throws SelectorParseException {
        String selectorString = ":(name,sectionAssociations)";
        String unbalancedString = selectorString + ")"; //append an unbalanced paren
        this.selectionConverter.convert(unbalancedString);
    }

    @Test(expected = SelectorParseException.class)
    public void testEmptyStringForKey() throws SelectorParseException {
        String selectorString = ":(:(test))";
        this.selectionConverter.convert(selectorString);
    }

    @Test(expected = SelectorParseException.class)
    public void testNonTrueFalseValueParsing() throws SelectorParseException {
        String selectorString = ":(someField:tru)"; //some guy spelled "true" wrong
        this.selectionConverter.convert(selectorString);
    }

}
