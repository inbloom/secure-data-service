package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

/**
 * JUnit test for Request class.
 *
 * @author wscott
 *
 */
public class RequestTest {

    private Request request;

    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);

    @Before
    public void setup() throws Exception {
        request = new Request(DOC, PARAMS, REPRESENTATIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParams() {
        new Request(DOC, null, REPRESENTATIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRepresentations() {
        new Request(DOC, PARAMS, null);
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, request.getParams());
    }

    @Test
    public void testGetRepresentations() {
        assertEquals(REPRESENTATIONS, request.getRepresentations());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(request.toString()));
    }
}
