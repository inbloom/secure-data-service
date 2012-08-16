package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Include class.
 *
 * @author wscott
 *
 */
public class IncludeTest {

    private Include include;

    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final String HREF = "href";

    @Before
    public void setup() throws Exception {
        include = new Include(HREF, DOC);
    }

    @Test(expected = NullPointerException.class)
    public void testNullHref() {
        new Include(null, DOC);
    }

    @Test
    public void testGetHref() {
        assertEquals(HREF, include.getHref());
    }

}
