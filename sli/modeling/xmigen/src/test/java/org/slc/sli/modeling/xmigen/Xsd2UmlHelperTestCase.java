package org.slc.sli.modeling.xmigen;

import junit.framework.TestCase;

public class Xsd2UmlHelperTestCase extends TestCase {
    
    public void testLhsReplace() {
        assertEquals("bcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", ""));
        assertEquals("bcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "A", ""));
        assertEquals("xbcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", "x"));
        assertEquals("xybcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", "xy"));
    }
    
    public void testRhsReplace() {
        assertEquals("abcdef", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", ""));
        assertEquals("abcdefx", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", "x"));
        assertEquals("abcdefxy", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", "xy"));
    }
    
    public void testInnerReplace() {
        assertEquals("abdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", ""));
        assertEquals("abxdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", "x"));
        assertEquals("abxydefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", "xy"));
    }
    
    public void testEmptyReplace() {
        assertEquals("abcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", ""));
        assertEquals("xaxbxcxdxexfxgx", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", "x"));
        assertEquals("xyaxybxycxydxyexyfxygxy", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", "xy"));
    }
    
    public void testPluralizeBasic() {
        assertEquals("reportCards", Xsd2UmlHelper.pluralize("reportCard"));
    }
    
    public void testPluralizeSpecialCases() {
        assertEquals("staff", Xsd2UmlHelper.pluralize("staff"));
        assertEquals("Staff", Xsd2UmlHelper.pluralize("Staff"));
    }
    
    public void testPluralizeEndsWithY() {
        assertEquals("gradebookEntries", Xsd2UmlHelper.pluralize("gradebookEntry"));
        assertEquals("Categories", Xsd2UmlHelper.pluralize("Category"));
        assertEquals("Agencies", Xsd2UmlHelper.pluralize("Agency"));
    }
    
    public void testPluralizeIdempotent() {
        assertEquals("grades", Xsd2UmlHelper.pluralize("grades"));
        assertEquals("reportCards", Xsd2UmlHelper.pluralize("reportCards"));
        assertEquals("learningObjectives", Xsd2UmlHelper.pluralize("learningObjectives"));
        assertEquals("learningStandards", Xsd2UmlHelper.pluralize("learningStandards"));
    }
    
    public void testCamelCase() {
        assertEquals("CIP", Xsd2UmlHelper.camelCase("CIP"));
        assertEquals("cipCode", Xsd2UmlHelper.camelCase("CIPCode"));
        assertEquals("CTE", Xsd2UmlHelper.camelCase("CTE"));
        assertEquals("GPA", Xsd2UmlHelper.camelCase("GPA"));
        assertEquals("ID", Xsd2UmlHelper.camelCase("ID"));
        assertEquals("IEP", Xsd2UmlHelper.camelCase("IEP"));
        assertEquals("LEA", Xsd2UmlHelper.camelCase("LEA"));
        assertEquals("URI", Xsd2UmlHelper.camelCase("URI"));
        assertEquals("URL", Xsd2UmlHelper.camelCase("URL"));
        assertEquals("URN", Xsd2UmlHelper.camelCase("URN"));
    }
}
