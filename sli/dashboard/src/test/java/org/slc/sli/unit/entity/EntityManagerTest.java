package org.slc.sli.unit.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.entity.EntityManager;
import org.slc.sli.entity.SimpleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class EntityManagerTest {
    
    private static Logger log = LoggerFactory.getLogger(EntityManagerTest.class);
    
    @Autowired
    private EntityManager entityManager;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void testOneEntity() {
        
        try {
            SimpleEntity entity = new SimpleEntity();
            entity.put("name", "entityName");
            List<SimpleEntity> entityList = new ArrayList<SimpleEntity>();
            entityList.add(entity);
            
            File tempFile = File.createTempFile("test", ".json");
            entityManager.toFile(tempFile.getPath(), entityList);
            List<SimpleEntity> verifyEntityList = entityManager.fromFile(tempFile.getPath());
            SimpleEntity verifyEntity = verifyEntityList.get(0);
            
            assertNotNull("Entity JSON conversion failed", verifyEntityList);
            assertEquals("Entity name corrupted during conversion", entity.get("name"), verifyEntity.get("name"));
            
        } catch (IOException ioException) {
            Assert.fail("IO exception occurred: " + ioException.getMessage());
        }
    }
    
    @Test
    public void testEntityListOfTwo() {
        
        try {
            SimpleEntity entityOne = new SimpleEntity();
            entityOne.put("name", "entityOne");
            SimpleEntity entityTwo = new SimpleEntity();
            entityTwo.put("name", "entityTwo");
            List<SimpleEntity> entityList = new ArrayList<SimpleEntity>();
            entityList.add(entityOne);
            entityList.add(entityTwo);
            
            File tempFile = File.createTempFile("test", ".json");
            entityManager.toFile(tempFile.getPath(), entityList);
            List<SimpleEntity> verifyEntityList = entityManager.fromFile(tempFile.getPath());
            SimpleEntity verifyEntityOne = verifyEntityList.get(0);
            SimpleEntity verifyEntityTwo = verifyEntityList.get(1);
            
            assertNotNull("Entity JSON conversion failed", verifyEntityList);
            assertEquals("Entity one name corrupted during conversion", entityOne.get("name"), verifyEntityOne.get("name"));
            assertEquals("Entity two name corrupted during conversion", entityTwo.get("name"), verifyEntityTwo.get("name"));
            
        } catch (IOException ioException) {
            Assert.fail("IO exception occurred: " + ioException.getMessage());
        }
    }
    
}
