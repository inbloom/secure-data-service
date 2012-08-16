package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.builders.NullBuilder;

/**
 * JUnit test for Link class.
 *
 * @author wscott
 *
 */

public class LinkTest {
    private Link link; // class under test

    private static final String RESOURCE_TYPE = "resourceType";
    private static final String REL = "rel";
    private static final String REV = "rev";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);

    @Before
    public void setup() throws Exception {
        link = new Link(RESOURCE_TYPE, REL, REV, DOC);
    }

    @Test (expected = NullPointerException.class)
    public void testNullResourceType() {
        new Link(null, REL, REV, DOC);
    }

    @Test (expected = NullPointerException.class)
    public void testNullRel() {
        new Link(RESOURCE_TYPE, null, REV, DOC);
    }

    @Test (expected = NullPointerException.class)
    public void testNullRev() {
        new Link(RESOURCE_TYPE, REL, null, DOC);
    }

    @Test (expected = NullPointerException.class)
    public void testNullDoc() {
        new Link(RESOURCE_TYPE, REL, REV, null);
    }

    @Test
    public void testGetResourceType() {
        assertEquals(RESOURCE_TYPE, link.getResourceType());
    }

    @Test
    public void testGetRelType() {
        assertEquals(REL, link.getRel());
    }

    @Test
    public void testGetRev() {
        assertEquals(REV, link.getRev());
    }

}
