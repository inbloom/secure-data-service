package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Representation class.
 *
 * @author wscott
 *
 */
public class RepresentationTest {

    private Representation representation;

    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<String> PROFILES = new ArrayList<String>(0);
    private static final String MEDIA_TYPE = "mediaType";
    private static final QName ELEMENT_NAME = new QName("elementName");
    private static final String ID = "id";

    @Before
    public void setup() throws Exception {
        representation = new Representation(ID, ELEMENT_NAME, MEDIA_TYPE, PROFILES, DOC, PARAMS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullMediaType() {
        new Representation(ID, ELEMENT_NAME, null, PROFILES, DOC, PARAMS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullProfiles() {
        new Representation(ID, ELEMENT_NAME, MEDIA_TYPE, null, DOC, PARAMS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParams() {
        new Representation(ID, ELEMENT_NAME, MEDIA_TYPE, PROFILES, DOC, null);
    }

    @Test
    public void testGetId() {
        assertEquals(ID, representation.getId());
    }

    @Test
    public void testGetMediaType() {
        assertEquals(MEDIA_TYPE, representation.getMediaType());
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, representation.getParams());
    }

    @Test
    public void testGetProfiles() {
        assertEquals(PROFILES, representation.getProfiles());
    }

    @Test
    public void testGetElementName() {
        assertEquals(ELEMENT_NAME, representation.getElementName());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(representation.toString()));
    }

}
