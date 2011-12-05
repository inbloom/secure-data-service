package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityServiceLayerTest {
    
    @Autowired
    private EntityDefinitionStore defs;
    
    @Before
    public void setUp() {
        defs.init();
    }
    
    @Test
    public void testMultipleEntities() {
        EntityDefinition studentDef = defs.lookupByResourceName("students");
        EntityService service = studentDef.getService();
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        EntityBody student2 = new EntityBody();
        student2.put("firstName", "Petra");
        student2.put("lastName", "Arkanian");
        EntityBody student3 = new EntityBody();
        student3.put("firstName", "Andrew");
        student3.put("lastName", "Wiggen");
        EntityBody student4 = new EntityBody();
        student4.put("firstName", "Julian");
        student4.put("lastName", "Delphiki");
        String id1 = service.create(student1);
        String id2 = service.create(student2);
        String id3 = service.create(student3);
        String id4 = service.create(student4);
        assertEquals(student1, service.get(id1));
        assertEquals(student2, service.get(id2));
        assertEquals(student3, service.get(id3));
        assertEquals(student4, service.get(id4));
        assertEquals(Arrays.asList(student1, student2, student3, student4),
                service.get(Arrays.asList(id1, id2, id3, id4)));
        List<String> firstSet = iterableToList(service.list(0, 2));
        assertEquals(2, firstSet.size());
        List<String> secondSet = iterableToList(service.list(2, 2));
        assertEquals(2, secondSet.size());
        Set<String> wholeSet = new HashSet<String>();
        wholeSet.addAll(firstSet);
        wholeSet.addAll(secondSet);
        assertEquals(new HashSet<String>(Arrays.asList(id1, id2, id3, id4)), wholeSet);
    }
    
    private <T> List<T> iterableToList(Iterable<T> itr) {
        List<T> result = new ArrayList<T>();
        for (T item : itr) {
            result.add(item);
        }
        return result;
    }
    
}
