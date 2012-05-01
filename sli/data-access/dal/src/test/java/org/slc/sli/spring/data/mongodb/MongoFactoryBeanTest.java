package org.slc.sli.spring.data.mongodb;

import junit.framework.Assert;

import com.mongodb.DB;
import com.mongodb.WriteConcern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for the MongoFactoryBean
 *
 * @author Ryan Farris <rfarris@wgen.net>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoFactoryBeanTest {

    @Value("${sli.mongodb.host}")
    String host;
    @Value("${sli.mongodb.port}")
    int port;
    @Value("${sli.mongodb.database}")
    String dbName;
    @Value("${sli.mongodb.user}")
    String userName;
    @Value("${sli.mongodb.pass}")
    String password;

    @Test
    public void testUriOnly() throws Exception {
        MongoFactoryBean mfb = new MongoFactoryBean();
        mfb.setMongoUri("mongodb://" + userName + ":" + password + "@" + host + ":" + port + "/" + dbName);
        MongoDbFactory dbf = mfb.getObject();
        DB db = dbf.getDb();
        Assert.assertEquals(dbName, db.getName());
        Assert.assertEquals(host, db.getMongo().getAddress().getHost());
        Assert.assertEquals(port, db.getMongo().getAddress().getPort());
    }

    @Test
    public void testOverride() throws Exception {
        MongoFactoryBean mfb = new MongoFactoryBean();
        mfb.setDbName("overridden");
        mfb.setUserName(null);
        mfb.setPassword(null);
        mfb.setWriteConcern(WriteConcern.FSYNC_SAFE);
        mfb.setMongoUri("mongodb://" + userName + ":" + password + "@" + host + ":" + port + "/" + dbName);
        MongoDbFactory dbf = mfb.getObject();
        DB db = dbf.getDb();
        Assert.assertEquals("overridden", db.getName());
        Assert.assertEquals(host, db.getMongo().getAddress().getHost());
        Assert.assertEquals(port, db.getMongo().getAddress().getPort());
        Assert.assertEquals(WriteConcern.FSYNC_SAFE, db.getWriteConcern());
    }
}
