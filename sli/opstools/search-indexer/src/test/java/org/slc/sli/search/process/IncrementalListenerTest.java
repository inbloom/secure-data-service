package org.slc.sli.search.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.impl.IncrementalListenerImpl;
import org.slc.sli.search.transform.IndexEntityConverter;

/**
 * Test class for the Sarje incremental update listener
 * 
 * @author dwu
 *
 */
public class IncrementalListenerTest {
    
    private String opLogInsert;
    private String opLogUpdate1;
    private String opLogUpdate2;
    private String opLogDelete;
    
    private IncrementalListenerImpl listener = new IncrementalListenerImpl();
    private IndexEntityConverter indexEntityConverter = new IndexEntityConverter();;
    
    @Before
    public void init() throws Exception {
        
        indexEntityConverter.setDecrypt(false);
        indexEntityConverter.setIndexConfigStore(new IndexConfigStore("index-config-test.json"));
        listener.setIndexEntityConverter(indexEntityConverter);
        
        // read in test oplog messages
        File inFile = new File(getClass().getClassLoader().getResource("studentOpLog.json").getFile());
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        opLogInsert = br.readLine();
        //System.out.println(opLogInsert);
        opLogUpdate1 = br.readLine();
        //System.out.println(opLogUpdate1);
    }
    
    
    /**
     * Test oplog insert -> index entity conversion
     */
    @Test
    public void testInsert() throws Exception {
        
        // convert to index entity
        IndexEntity entity = listener.convertToEntity(opLogInsert);
        
        // check result
        Assert.assertEquals(entity.getActionValue(), "index");
        Assert.assertEquals(entity.getId(), "4ef33d4356e3e757e5c3662e6a79ddbfd8b31866_id");
        Assert.assertEquals(entity.getType(), "student");
        Assert.assertEquals(entity.getIndex(), "midgar");
        Map<String, Object> name = (Map<String, Object>) entity.getBody().get("name"); 
        Assert.assertEquals(name.get("firstName"), "ESTRING:oF9iD6JYVIXWiLxhlEY5Rw==");
        Assert.assertEquals(name.get("lastSurname"), "ESTRING:B8eYiF6KTM4Fab9/A1lHsQ==");
    }
    
    
    /**
     * Test oplog update -> index entity conversion
     * Only updates the student's last name
     */
    @Test
    public void testUpdate() throws Exception {
        
        // convert to index entity
        IndexEntity entity = listener.convertToEntity(opLogUpdate1);
        
        // check result
        Assert.assertEquals(entity.getActionValue(), "update");
        Assert.assertEquals(entity.getId(), "4ef33d4356e3e757e5c3662e6a79ddbfd8b31866_id");
        Assert.assertEquals(entity.getType(), "student");
        Assert.assertEquals(entity.getIndex(), "midgar");
        Map<String, Object> name = (Map<String, Object>) entity.getBody().get("name"); 
        // updated last name
        Assert.assertEquals(name.get("lastSurname"), "ESTRING:hRPQJLZBfU/5g2ifTFMZrA==");
    }
    
    @Test
    public void testUpdate2() throws Exception {
        
        // convert to index entity
        
        // check result
        
    }
    
    /**
     * Test oplog delete -> index entity conversion
     */
    @Test
    public void testDelete() throws Exception {
        
        // convert to index entity
        //IndexEntity entity = listener.convertToEntity(opLogDelete);
        
        // check result
        
    }
    
    /**
     * Test filtering of oplog info
     */
    public void testFilter() {
        
    }
    
}