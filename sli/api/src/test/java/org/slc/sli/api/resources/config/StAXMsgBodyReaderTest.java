/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.resources.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.representation.EntityBody;

/**
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StAXMsgBodyReaderTest {

    @Autowired
    private StAXMsgBodyReader reader; // class under test

    @Before
    public void setup() {
        reader = new StAXMsgBodyReader();
    }

    @Test
    public void testValues() {
        final String xmlValues = "<test><k1>v1</k1><k2>v2</k2></test>";
        EntityBody body = null;
        try {
            body = deserialize(xmlValues);
        } catch (XMLStreamException e) {
            fail(e.getMessage());
        }

        assertNotNull(body);
        assertEquals("v1", body.get("k1"));
        assertEquals("v2", body.get("k2"));
    }

    @Test
    public void testLists() {
        final String xmlList = "<test xmlns:sli=\"urn:sli\"><l1 sli:member=\"true\">v1</l1><l1 sli:member=\"true\">v2</l1></test>";
        EntityBody body = null;
        try {
            body = deserialize(xmlList);
        } catch (XMLStreamException e) {
            fail(e.getMessage());
        }

        assertNotNull(body);
        assertTrue("Should be a list", body.get("l1") instanceof List);
        @SuppressWarnings("unchecked")
        final List<String> l1Values = (List<String>) body.get("l1");
        assertTrue(l1Values.contains("v1"));
        assertTrue(l1Values.contains("v2"));
    }

    @Test
    public void testMaps() {
        final String xmlMaps = "<test>"
                + "<key1>"
                + "<ek1>ev1</ek1>"
                + "<ek2>ev2</ek2>"
                + "</key1>"
                + "</test>";

        EntityBody body = null;
        try {
            body = deserialize(xmlMaps);
        } catch (XMLStreamException e) {
            fail(e.getMessage());
        }

        assertNotNull(body);
        assertTrue("Should be a map", body.get("key1") instanceof Map);

        @SuppressWarnings("unchecked")
        final Map<String, Object> map = (Map<String, Object>) body.get("key1");
        assertEquals("ev1", map.get("ek1"));
        assertEquals("ev2", map.get("ek2"));
    }

    @Test
    public void testSomethingComplex() {
        final String complexXML =
                "<school xmlns:sli=\"urn:sli\">\n"
                        + "  <id>8cc0a1ac-ccb5-dffc-1d74-32964722179b</id>\n"
                        + "  <schoolCategories sli:member=\"true\">Middle School</schoolCategories>\n"
                        + "  <gradesOffered sli:member=\"true\">Sixth grade</gradesOffered>\n"
                        + "  <gradesOffered sli:member=\"true\">Eighth grade</gradesOffered>\n"
                        + "  <gradesOffered sli:member=\"true\">Seventh grade</gradesOffered>\n"
                        + "  <organizationCategories sli:member=\"true\">School</organizationCategories>\n"
                        + "  <address sli:member=\"true\">\n"
                        + "    <addressType>Physical</addressType>\n"
                        + "    <streetNumberName>456 Blah Street</streetNumberName>\n"
                        + "    <city>Las Vegas</city>\n"
                        + "    <stateAbbreviation>NV</stateAbbreviation>\n"
                        + "    <postalCode>66666</postalCode>\n"
                        + "    <nameOfCounty>Vegas County</nameOfCounty>\n"
                        + "  </address>\n"
                        + "  <address sli:member=\"true\">\n"
                        + "    <addressType>Physical</addressType>\n"
                        + "    <streetNumberName>123 Blah Street</streetNumberName>\n"
                        + "    <city>Durham</city>\n"
                        + "    <stateAbbreviation>NC</stateAbbreviation>\n"
                        + "    <postalCode>66666</postalCode>\n"
                        + "    <nameOfCounty>Durham</nameOfCounty>\n"
                        + "  </address>\n"
                        + "  <parentEducationAgencyReference>bd086bae-ee82-4cf2-baf9-221a9407ea07</parentEducationAgencyReference>\n"
                        + "  <stateOrganizationId>152901004</stateOrganizationId>\n"
                        + "  <entityType>school</entityType>\n"
                        + "  <telephone sli:member=\"true\">\n"
                        + "    <institutionTelephoneNumberType>Main</institutionTelephoneNumberType>\n"
                        + "    <telephoneNumber>(333) 344-7777</telephoneNumber>\n"
                        + "  </telephone>\n"
                        + "  <nameOfInstitution>Purple Middle School</nameOfInstitution>\n"
                        + "</school>\n";

        EntityBody body = null;
        try {
            body = deserialize(complexXML);
        } catch (XMLStreamException e) {
            fail(e.getMessage());
        }

        assertNotNull(body);

        Object address = body.get("address");
        assertTrue(address instanceof List);
        assertEquals(2, ((List) address).size());

        Object telephone = body.get("telephone");
        assertTrue(telephone instanceof List);
        assertEquals(1, ((List) telephone).size());
        @SuppressWarnings("unchecked")
        Map<String, Object> telephoneHash = (Map<String, Object>) ((List) telephone).get(0);
        assertEquals("(333) 344-7777", telephoneHash.get("telephoneNumber").toString());

        Object id = body.get("id");
        assertTrue(id instanceof String);
        assertEquals("8cc0a1ac-ccb5-dffc-1d74-32964722179b", id.toString());
    }

    @Test(expected = XMLStreamException.class)
    public void testMissingEndTag() throws XMLStreamException {
        // Missing ending tag
        final String xmlValues = "<test><k1>v1</k1><k2>v2</k2>";
        final EntityBody body = deserialize(xmlValues);
    }

    @Test(expected = XMLStreamException.class)
    public void testMalformedXML() throws XMLStreamException {
        final String xmlValues = "<test><k1>v1</k1><k2>v2</k2></tset>";
        final EntityBody body = deserialize(xmlValues);
    }

    @Test
    public void testProcessingInstruction() {
        final String xmlValues = "<?xml version=\"1.0\" ?><test><k1>v1</k1><k2>v2</k2></test>";
        EntityBody body = null;
        try {
            body = deserialize(xmlValues);
        } catch (XMLStreamException e) {
            fail();
        }

        assertNotNull(body);
    }

    private EntityBody deserialize(String xmlString) throws XMLStreamException {
        final ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")));
        return reader.deserialize(xmlStream);
    }
}
