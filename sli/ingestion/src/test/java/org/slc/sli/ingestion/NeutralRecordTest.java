package org.slc.sli.ingestion;

import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class NeutralRecordTest {

    @Test
    public void testConstruct() throws Exception {

        NeutralRecord nr = new NeutralRecord();

        nr.setSourceId("source");
        nr.setJobId("job");
        nr.setLocalId("12345");
        nr.setRecordType("Student");
        nr.getAttributes().put("firstName", "Jane");
        nr.getAttributes().put("lastName", "Doe");
        nr.getAttributes().put("birthDate", "1971-11-02");
        nr.getLocalParentIds().put("School", "876543");
        nr.setAttributesCrc("e709812e43fffb2c12891c0fe94a4450");

        // not much to assert so far -
        assertEquals(nr.getSourceId(), "source");
        assertEquals(nr.getJobId(), "job");
        assertEquals(nr.getLocalId(), "12345");
        assertEquals(nr.getRecordType(), "Student");
        assertEquals(nr.getAttributes().size(), 3);
        assertEquals(nr.getAttributes().get("firstName"), "Jane");
        assertEquals(nr.getAttributes().get("lastName"), "Doe");
        assertEquals(nr.getAttributes().get("birthDate"), "1971-11-02");
        assertEquals(nr.getLocalParentIds().size(), 1);
        assertEquals(nr.getLocalParentIds().get("School"), "876543");
        assertEquals(nr.getAttributesCrc(), "e709812e43fffb2c12891c0fe94a4450");

        // make sure all the values we set are represented accurately in
        // toString() as a json structure, that reinflates to an object with
        // equal values
        ObjectMapper mapper = new ObjectMapper();
        NeutralRecord nr2 = mapper
                .readValue(nr.toString(), NeutralRecord.class);
        assertEquals(nr2.getSourceId(), nr.getSourceId());
        assertEquals(nr2.getJobId(), nr.getJobId());
        assertEquals(nr2.getLocalId(), nr.getLocalId());
        assertEquals(nr2.getRecordType(), nr.getRecordType());
        assertEquals(nr2.getAttributes(), nr.getAttributes());
        assertEquals(nr2.getLocalParentIds(), nr.getLocalParentIds());
        assertEquals(nr2.getAttributesCrc(), nr.getAttributesCrc());

    }

}
