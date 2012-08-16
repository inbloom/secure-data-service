package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.xdm.DmNode;

/**
 *
 * JUnit test for Documentation class.
 *
 * @author wscott
 *
 */
public class DocumentationTest {

    private Documentation documentation;  //class under test
    private static final String TITLE = "doctest";
    private static final String LANGUAGE = "en";
    private static final List<DmNode> CONTENTS = new ArrayList<DmNode>(0);

    @Before
    public void setup() {
        documentation = new Documentation(TITLE, LANGUAGE, CONTENTS);
    }

    @Test (expected = NullPointerException.class)
    public void testNullContents() {
        new Documentation(TITLE, LANGUAGE, null);
    }

    @Test
    public void testGetTitle() {
        assertEquals(TITLE, documentation.getTitle());
    }

    @Test
    public void testGetLanguage() {
        assertEquals(LANGUAGE, documentation.getLanguage());
    }

    @Test
    public void testGetContents() {
        assertEquals(CONTENTS, documentation.getContents());
    }

    @Test
    public void testToString() {
        String expected = "{language : " + LANGUAGE + ", title : " + TITLE + ", contents : []}";
        assertEquals(expected, documentation.toString());
    }

}
