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
    
}
