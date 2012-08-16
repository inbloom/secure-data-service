package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Response class.
 *
 * @author wscott
 *
 */
public class ResponseTest {
    private static final ArrayList<String> STATUS_CODES = new ArrayList<String>(0);
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);
    private Response response;

    @Before
    public void setup() throws Exception {
        response = new Response(STATUS_CODES, DOC, PARAMS, REPRESENTATIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStatusCodes() {
        new Response(null, DOC, PARAMS, REPRESENTATIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParams() {
        new Response(STATUS_CODES, DOC, null, REPRESENTATIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRepresentations() {
        new Response(STATUS_CODES, DOC, PARAMS, null);
    }

    @Test
    public void testGetStatusCodes() {
        assertEquals(STATUS_CODES, response.getStatusCodes());
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, response.getParams());
    }

    @Test
    public void testGetRepresentations() {
        assertEquals(REPRESENTATIONS, response.getRepresentations());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(response.toString()));
    }

}
