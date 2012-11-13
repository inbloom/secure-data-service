package org.slc.sli.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.mongodb.DBCollection;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.search.entity.IndexEntity.Action;

public class OplogConverterTest {
    private ObjectMapper mapper = null;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    public void testSet() throws JsonParseException, JsonMappingException, IOException {
        Object obj = NestedMapUtil.get(new DotPath("body.principal.realm"),
                getEntity("setOplog"));
        Assert.assertEquals("2012dd-a4121e59-243a-11e2-beb4-3c07546832b4", obj);
    }

    // @Test
    public void testPull() throws JsonParseException, JsonMappingException, IOException {
        Object obj = NestedMapUtil.get(new DotPath("schools"), getEntity("pullOplog"));
        Assert.assertTrue(obj instanceof List);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) obj;
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testAddToSetEach() throws JsonParseException, JsonMappingException, IOException {
        Object obj = NestedMapUtil.get(new DotPath("metaData.edOrgs"),
                getEntity("addToSetEachOplog"));
        Assert.assertTrue(obj instanceof List);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) obj;
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("884daa27d806c2d725bc469b273d840493f84b4d_id", list.get(0));
        Assert.assertEquals("1b223f577827204a1c7e9c851dba06bea6b031fe_id", list.get(1));
        Assert.assertEquals("a13489364c2eb015c219172d561c62350f0453f3_id", list.get(2));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testPushAll() throws JsonParseException, JsonMappingException, IOException {
        Object obj = NestedMapUtil.get(new DotPath("schools"), getEntity("pushAllOplog"));
        Assert.assertTrue(obj instanceof List);
        List list = (List) obj;
        Assert.assertEquals(2, list.size());
        Map<String, Object> obj0 = (Map<String, Object>) list.get(0);
        Assert.assertEquals("Kindergarten", obj0.get("entryGradeLevel"));
        Map<String, Object> obj1 = (Map<String, Object>) list.get(1);
        List<Object> list1 = (List<Object>) obj1.get("edOrgs");
        Assert.assertEquals(3, list1.size());
        Assert.assertEquals("1b223f577827204a1c7e9c851dba06bea6b031fe_id", list1.get(2));
    }

    private Map<String, Object> getEntity(String file) throws JsonParseException, JsonMappingException, IOException {
        String opLog = getOplogJsonFile(file);
        Map<String, Object> opLogs = mapper.readValue(opLog, new TypeReference<Map<String, Object>>() {
        });
        return OplogConverter.getEntity(Action.UPDATE, opLogs);
    }

    private String getOplogJsonFile(String name) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            File jsonFile = new File(DBCollection.class.getClassLoader().getResource("oplog/" + name + ".json")
                    .getFile());
            br = new BufferedReader(new FileReader(jsonFile));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {

        }
        return sb.toString();
    }

}
