package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.context.ApplicationContext;

/**
 * EntityWriteConverter unit-tests.
 * 
 * @author okrook
 * 
 */
public class EntityWriteConverterTest {
    
    @InjectMocks
    EntityWriteConverter converter = new EntityWriteConverter();
    
    @Mock
    EntityEncryption encryptor;
    List<Object[]> encryptCalls = new ArrayList<Object[]>();
    
    @Mock
    ApplicationContext mockContext;
    
    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(encryptor.encrypt(Mockito.anyString(), Mockito.anyMap())).thenAnswer(
                new Answer<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> answer(InvocationOnMock invocation) throws Throwable {
                        encryptCalls.add(invocation.getArguments());
                        return (Map<String, Object>) invocation.getArguments()[1];
                    }
                });
        Mockito.when(mockContext.getBean("entityEncryption", EntityEncryption.class)).thenReturn(encryptor);
    }
    
    @Test
    public void testEntityConvert() {
        Entity e = Mockito.mock(Entity.class);
        
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("field1", "field1");
        body.put("field2", "field2");
        
        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("meta1", "field1");
        meta.put("meta2", "field2");
        
        Mockito.when(e.getType()).thenReturn("collection");
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getMetaData()).thenReturn(meta);
        
        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);
        
        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
        
        Assert.assertEquals(1, encryptCalls.size());
        Assert.assertEquals(2, encryptCalls.get(0).length);
        Assert.assertEquals(e.getType(), encryptCalls.get(0)[0]);
        Assert.assertEquals(e.getBody(), encryptCalls.get(0)[1]);
    }
    
    @Test
    public void testMongoEntityConvert() {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("field1", "field1");
        body.put("field2", "field2");
        
        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("meta1", "field1");
        meta.put("meta2", "field2");
        
        MongoEntity e = new MongoEntity("collection", UUID.randomUUID().toString(), body, meta);
        
        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);
        
        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
        
        Assert.assertEquals(1, encryptCalls.size());
        Assert.assertEquals(2, encryptCalls.get(0).length);
        Assert.assertEquals(e.getType(), encryptCalls.get(0)[0]);
        Assert.assertEquals(e.getBody(), encryptCalls.get(0)[1]);
    }
    
    private static void assertSame(Map<?, ?> m1, Map<?, ?> m2) {
        if (m1 == null && m2 == null) {
            return;
        }
        
        Assert.assertNotNull(m1);
        Assert.assertNotNull(m2);
        
        if (m1 == m2) {
            return;
        }
        
        Assert.assertArrayEquals(m1.keySet().toArray(), m2.keySet().toArray());
        
        for (Map.Entry<?, ?> e : m1.entrySet()) {
            Assert.assertEquals(e.getValue(), m2.get(e.getKey()));
        }
    }
    
}
