package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoQueryConverterTest {
    
    @Test
    public void testKeyPrefixing() {
        
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("metadata.x", "=", "1");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("metadata.x", "=", "1", false);
        NeutralCriteria neutralCriteria3 = new NeutralCriteria("_id", "=", "1");
        NeutralCriteria neutralCriteria4 = new NeutralCriteria("metadata._id", "=", "1");

        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria1), "body.metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria2), "metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria3), "_id");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria4), "body.metadata._id");
    }

}
