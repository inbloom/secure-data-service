package org.slc.sli.ingestion.transformation;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Unit Test for SmooksEdFi2SLITransformer
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/transformation-context.xml" })
public class SmooksEdFi2SLITransformerTest {

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    @Test
    public void testDirectMapping() {
        NeutralRecord directlyMapped = new NeutralRecord();
        directlyMapped.setRecordType("directEntity");
        directlyMapped.setAttributeField("field2", "Test String");

        List<? extends Entity> result = transformer.handle(directlyMapped);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Test String", result.get(0).getBody().get("field1"));
    }
}
