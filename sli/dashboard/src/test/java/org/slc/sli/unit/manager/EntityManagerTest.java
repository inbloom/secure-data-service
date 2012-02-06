package org.slc.sli.unit.manager;

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
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;

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
            GenericEntity entity = new GenericEntity();
            entity.put("name", "entityName");
            List<GenericEntity> entityList = new ArrayList<GenericEntity>();
            entityList.add(entity);
            
            File tempFile = File.createTempFile("test", ".json");
            entityManager.toFile(tempFile.getPath(), entityList);
            List<GenericEntity> verifyEntityList = entityManager.fromFile(tempFile.getPath());
            GenericEntity verifyEntity = verifyEntityList.get(0);
            
            assertNotNull("Entity JSON conversion failed", verifyEntityList);
            assertEquals("Entity name corrupted during conversion", entity.get("name"), verifyEntity.get("name"));
            
        } catch (IOException ioException) {
            Assert.fail("IO exception occurred: " + ioException.getMessage());
        }
    }
    
    @Test
    public void testEntityListOfTwo() {
        
        try {
            GenericEntity entityOne = new GenericEntity();
            entityOne.put("name", "entityOne");
            GenericEntity entityTwo = new GenericEntity();
            entityTwo.put("name", "entityTwo");
            List<GenericEntity> entityList = new ArrayList<GenericEntity>();
            entityList.add(entityOne);
            entityList.add(entityTwo);
            
            File tempFile = File.createTempFile("test", ".json");
            entityManager.toFile(tempFile.getPath(), entityList);
            List<GenericEntity> verifyEntityList = entityManager.fromFile(tempFile.getPath());
            GenericEntity verifyEntityOne = verifyEntityList.get(0);
            GenericEntity verifyEntityTwo = verifyEntityList.get(1);
            
            assertNotNull("Entity JSON conversion failed", verifyEntityList);
            assertEquals("Entity one name corrupted during conversion", entityOne.get("name"), verifyEntityOne.get("name"));
            assertEquals("Entity two name corrupted during conversion", entityTwo.get("name"), verifyEntityTwo.get("name"));
            
        } catch (IOException ioException) {
            Assert.fail("IO exception occurred: " + ioException.getMessage());
        }
    }
    
}
