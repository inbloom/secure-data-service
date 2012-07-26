package org.slc.sli.api.service.query;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slc.sli.api.selectors.model.SelectorParseException;

public class Selector2MapOfMapsTest {
    
    private SelectionConverter selectionConverter = new Selector2MapOfMaps();
    
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
    public void yetAnotherTest() throws SelectorParseException {
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
    
    @Test(expected=SelectorParseException.class)
    public void testInvalidSyntax() throws SelectorParseException {
        this.selectionConverter.convert(":(");
    }
    
    @Test(expected=SelectorParseException.class)
    public void testEmptyStrings() throws SelectorParseException {
        this.selectionConverter.convert(":(,,)");
    }

    @Test(expected=SelectorParseException.class)
    public void testUnbalancedParens() throws SelectorParseException {
        Selector2MapOfMaps.getMatchingClosingParenIndex("((", 0);
    }

    @Test(expected=SelectorParseException.class)
    public void testUnbalancedParens2() throws SelectorParseException {
        Selector2MapOfMaps.getMatchingClosingParenIndex(")", 0);
    }
    
    
}
