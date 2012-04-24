package org.slc.sli.ingestion.dal;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A Junit test for IndexResourcePatternResolver
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IndexResourcePatternResolverTest {
    @Autowired
    IndexResourcePatternResolver irpr;

    @Test
    public void testFindAllIndexes() {
        String indexRootDir = "classpath:mongoIndexes/**/*.json";
        List<MongoIndexConfig> indexes = irpr.findAllIndexes(indexRootDir);

        Assert.assertEquals(2, indexes.size());
        Assert.assertEquals("student", indexes.get(1).getCollection());
        Assert.assertEquals("student", indexes.get(0).getCollection());

        List<Map<String, String>> fields1 = indexes.get(0).getIndexFields();
        Assert.assertEquals(3, fields1.size());
        Assert.assertEquals("body.name", fields1.get(0).get("name"));
        Assert.assertEquals("body.sex", fields1.get(1).get("name"));
        Assert.assertEquals("body.birthDate", fields1.get(2).get("name"));


        List<Map<String, String>> fields2 = indexes.get(1).getIndexFields();
        Assert.assertEquals(1, fields2.size());
        Assert.assertEquals("metaData.tenantId", fields2.get(0).get("name"));


    }

}
