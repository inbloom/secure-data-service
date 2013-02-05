/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dal.encrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Test for EntityEncryption class.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(JUnit4.class)
public class EntityEncryptionTest {
    
    @InjectMocks
    EntityEncryption encryptor = new EntityEncryption();
    
    @Mock
    SchemaRepository schemaRepo;
    
    @Mock
    Cipher unbreakableCipher;
    
    AppInfo piiAnnotation;
    NeutralSchemaFactory schemaFactory = new NeutralSchemaFactory();
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        piiAnnotation = new AppInfo(null);
        piiAnnotation.put("PersonallyIdentifiableInfo", "true");
        Mockito.when(unbreakableCipher.encrypt(Mockito.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object object = invocation.getArguments()[0];
                return "E:" + object.toString();
            }
        });
        Mockito.when(unbreakableCipher.decrypt(Mockito.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object object = invocation.getArguments()[0];
                return object.toString().replace("E:", "");
            }
        });
    }
    
    /*
     * single encrypted field
     * single unencrypted field
     */
    @Test
    public void testSingleField() {
        Map<String, Object> test = createMap("encrypted", "A");
        test.put("notEncrypted", "B");
        
        NeutralSchema testSchema = schemaFactory.createSchema("complex");
        NeutralSchema encryptedSchema = schemaFactory.createSchema("string");
        encryptedSchema.addAnnotation(piiAnnotation);
        testSchema.addField("encrypted", encryptedSchema);
        testSchema.addField("notEncrypted", schemaFactory.createSchema("string"));
        
        Mockito.when(schemaRepo.getSchema("test")).thenReturn(testSchema);
        
        Map<String, Object> e = encryptor.encrypt("test", test);
        assertNotNull(e);
        assertEquals("E:A", e.get("encrypted"));
        assertEquals("B", e.get("notEncrypted"));
        
        Map<String, Object> d = encryptor.decrypt("test", e);
        assertNotNull(d);
        assertEquals("A", d.get("encrypted"));
        assertEquals("B", d.get("notEncrypted"));
    }
    
    /*
     * encrypted complex type
     * encrypt one member of complex type
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testComplexType() {
        Map<String, Object> test = createMap("complex", createMap("a", "A"));
        ((Map<String, Object>) test.get("complex")).put("b", "B");
        
        NeutralSchema testSchemaAll = schemaFactory.createSchema("complex");
        NeutralSchema complexSchemaPii = schemaFactory.createSchema("complex");
        complexSchemaPii.addAnnotation(piiAnnotation);
        complexSchemaPii.addField("a", schemaFactory.createSchema("string"));
        complexSchemaPii.addField("b", schemaFactory.createSchema("string"));
        testSchemaAll.addField("complex", complexSchemaPii);
        
        NeutralSchema testSchemaSingle = schemaFactory.createSchema("complex");
        NeutralSchema complexSchema = schemaFactory.createSchema("complex");
        NeutralSchema encryptedString = schemaFactory.createSchema("string");
        encryptedString.addAnnotation(piiAnnotation);
        complexSchema.addField("a", encryptedString);
        complexSchema.addField("b", schemaFactory.createSchema("string"));
        testSchemaSingle.addField("complex", complexSchema);
        
        Mockito.when(schemaRepo.getSchema("testAll")).thenReturn(testSchemaAll);
        Mockito.when(schemaRepo.getSchema("testSingle")).thenReturn(testSchemaSingle);
        
        Map<String, Object> e = encryptor.encrypt("testAll", test);
        assertEquals("E:A", ((Map<?, ?>) e.get("complex")).get("a"));
        assertEquals("E:B", ((Map<?, ?>) e.get("complex")).get("b"));
        Map<String, Object> d = encryptor.decrypt("testAll", e);
        assertEquals("A", ((Map<?, ?>) d.get("complex")).get("a"));
        assertEquals("B", ((Map<?, ?>) d.get("complex")).get("b"));
        
        e = encryptor.encrypt("testSingle", test);
        assertEquals("E:A", ((Map<?, ?>) e.get("complex")).get("a"));
        assertEquals("B", ((Map<?, ?>) e.get("complex")).get("b"));
        d = encryptor.decrypt("testSingle", e);
        assertEquals("A", ((Map<?, ?>) d.get("complex")).get("a"));
        assertEquals("B", ((Map<?, ?>) d.get("complex")).get("b"));
    }
    
    /*
     * encrypted list of simple items
     * encrypt single member of complex type inside list
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testList() {
        List<String> stringList = Arrays.asList(new String[] { "A", "B" });
        Map<String, Object> simpleListData = createMap("list", stringList);
        
        List<Map> mapList = Arrays
                .asList(new Map[] { createMap("a", "A1", "b", "B1"), createMap("a", "A2", "b", "B2") });
        Map<String, Object> complexListData = createMap("list", mapList);
        
        NeutralSchema simpleSchema = schemaFactory.createSchema("complex");
        ListSchema simpleListSchema = (ListSchema) schemaFactory.createSchema("list");
        simpleListSchema.addAnnotation(piiAnnotation);
        simpleListSchema.getList().add(schemaFactory.createSchema("string"));
        simpleListSchema.updateAnnotations();
        simpleSchema.addField("list", simpleListSchema);
        
        NeutralSchema complexSchema = schemaFactory.createSchema("complex");
        ListSchema complexList = (ListSchema) schemaFactory.createSchema("list");
        NeutralSchema complex = schemaFactory.createSchema("complex");
        NeutralSchema encryptedSchema = schemaFactory.createSchema("string");
        encryptedSchema.addAnnotation(piiAnnotation);
        complex.addField("a", encryptedSchema);
        complex.addField("b", schemaFactory.createSchema("string"));
        complexList.getList().add(complex);
        complexList.updateAnnotations();
        complexSchema.addField("list", complexList);
        
        Mockito.when(schemaRepo.getSchema("testSimple")).thenReturn(simpleSchema);
        Mockito.when(schemaRepo.getSchema("testComplex")).thenReturn(complexSchema);
        
        Map<String, Object> e = encryptor.encrypt("testSimple", simpleListData);
        List<?> list = (List<?>) e.get("list");
        assertEquals("E:A", list.get(0));
        assertEquals("E:B", list.get(1));
        
        Map<String, Object> d = encryptor.decrypt("testSimple", e);
        list = (List<?>) d.get("list");
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        
        e = encryptor.encrypt("testComplex", complexListData);
        List<Map<String, Object>> l = (List<Map<String, Object>>) e.get("list");
        assertEquals("E:A1", l.get(0).get("a"));
        assertEquals("B1", l.get(0).get("b"));
        assertEquals("E:A2", l.get(1).get("a"));
        assertEquals("B2", l.get(1).get("b"));
        
        d = encryptor.decrypt("testComplex", e);
        l = (List<Map<String, Object>>) d.get("list");
        assertEquals("A1", l.get(0).get("a"));
        assertEquals("B1", l.get(0).get("b"));
        assertEquals("A2", l.get(1).get("a"));
        assertEquals("B2", l.get(1).get("b"));
    }
    
    /*
     * encrypt single choice value
     * encrypt list of choice value
     * list of choice value, only one choice is encrypted
     */
    @Test
    public void testChoice() {
        // TODO
    }
    
    private Map<String, Object> createMap(String key, Object value) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        return map;
    }
    
    private Map<String, Object> createMap(Object... values) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < values.length; i += 2) {
            map.put((String) values[i], values[i + 1]);
        }
        return map;
    }
}
