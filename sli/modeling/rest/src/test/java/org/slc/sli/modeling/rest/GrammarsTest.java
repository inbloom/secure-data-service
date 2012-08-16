package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Grammars class.
 *
 * @author wscott
 *
 */
public class GrammarsTest {

    private Grammars grammars;

    private static final ArrayList<Include> INCLUDES = new ArrayList<Include>(0);
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);

    @Before
    public void setup() throws Exception {
        grammars = new Grammars(DOC, INCLUDES);
    }

    @Test(expected = NullPointerException.class)
    public void testNullIncludes() {
        new Grammars(DOC, null);
    }

    @Test
    public void testGetIncludes() {
        assertEquals(INCLUDES, grammars.getIncludes());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(grammars.toString()));
    }

}
