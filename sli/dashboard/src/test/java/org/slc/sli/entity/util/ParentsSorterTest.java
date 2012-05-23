package org.slc.sli.entity.util;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * test for ParentsSorter class
 * 
 * @author tosako
 * 
 */
public class ParentsSorterTest {
    
    @Test
    public void testSort() {
        List<GenericEntity> entities = new LinkedList<GenericEntity>();
        List<LinkedHashMap<String, Object>> studentParentsAssocistion = new LinkedList<LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
        obj.put(Constants.ATTR_RELATION, "Father");
        studentParentsAssocistion.add(obj);
        GenericEntity entity = new GenericEntity();
        entity.put(Constants.ATTR_STUDENT_PARENT_ASSOCIATION, studentParentsAssocistion);
        entities.add(entity);
        
        obj = new LinkedHashMap<String, Object>();
        obj.put(Constants.ATTR_RELATION, "Mother");
        studentParentsAssocistion = new LinkedList<LinkedHashMap<String, Object>>();
        studentParentsAssocistion.add(obj);
        entity = new GenericEntity();
        entity.put(Constants.ATTR_STUDENT_PARENT_ASSOCIATION, studentParentsAssocistion);
        entities.add(entity);
        
        ParentsSorter.sort(entities);
        List<LinkedHashMap<String, Object>> objTest = (List<LinkedHashMap<String, Object>>) entities.get(0).get(
                Constants.ATTR_STUDENT_PARENT_ASSOCIATION);
        Assert.assertEquals("Mother", objTest.get(0).get(Constants.ATTR_RELATION));
    }
}
