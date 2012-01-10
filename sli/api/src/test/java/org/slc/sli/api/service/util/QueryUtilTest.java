package org.slc.sli.api.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * QueryUtil tests for the service layer in api.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class QueryUtilTest {
    
    @Test
    public void testStringToQuery() {
        String queryString = "name.firstName=Jane&name.lastSurname=Doe&age>20";
        Query query = QueryUtil.stringToQuery(queryString);
        assertNotNull(query);
        DBObject queryObject = query.getQueryObject();
        assertEquals(queryObject.get("body.name.firstName"), "Jane");
        assertEquals(queryObject.get("body.name.lastSurname"), "Doe");
        assertEquals(queryObject.get("body.age"), new BasicDBObject("$gt", "20"));
    }
}
