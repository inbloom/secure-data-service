package org.slc.sli.unit.manager;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.Manager;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.util.DashboardException;

/**
 * Test CustomizationAssemblyFactory features
 * @author agrebneva
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class CustomizationAssemblyFactoryTest {
    @Autowired
    ApplicationContext applicationContext;

    private static final String DEFAULT_LAYOUT_JSON = 
            "{id : 'studentProfile', type: 'LAYOUT', " 
          + " data :{entity: 'mock', alias: 'mock' }, " 
          + " items: [{id : 'panel', name: 'Student Info', type: 'PANEL'},"
          + "         {id: 'tab2', condition: {field: 'gender', value: ['male']}, name: 'Attendance and Discipline', type : 'TAB', " 
          + "          items: [{id : 'panel', type: 'PANEL'}]}]}";
    private static final String DEFAULT_LAYOUT_TOO_DEEP_JSON = 
            "{id : 'deep', type: 'LAYOUT', " 
          + " data :{entity: 'mock', alias: 'mock' }, " 
          + " items: [{id : 'deep', name: 'Student Info', type: 'PANEL'}]}";
    private static final String DEFAULT_PANEL_JSON = 
            "{id : 'panel', type: 'PANEL', condition: {field: 'gender', value: ['male']}, data :{entity: 'test',alias: 'mock' }}";
    
    private static final String DEFAULT_PANEL1_JSON = 
            "{id : 'panel1', type: 'PANEL', condition: {field: 'gradeNumeric', value: [1,2,5]}, data :{entity: 'test',alias: 'mock' }}";
    
    private static GenericEntity simpleMaleStudentEntity;
    private static GenericEntity simpleFemaleStudentEntity;
    
    private static Map<String, Config> configMap;
    
    private static Map<String, GenericEntity> sampleEntityMap;
    
    /**
     * Expose some methods
     * @author agrebneva
     *
     */
    class MockCustomizationAssemblyFactoryImpl extends CustomizationAssemblyFactoryImpl {
        
        protected String getTokenId() {
            return "1";
        }
        
        protected Config getConfig(String componentId) {
            
            return configMap.get(componentId);
        }
        
        protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            return sampleEntityMap.get((String) entityKey);
        }
        
        public boolean hasInvokableSet(String entityRef) {
            return getInvokableSet(entityRef) != null;
        }
        
        public GenericEntity getDataComponentForTest(String componentId, Object entityKey, Config.Data config) { 
            return super.getDataComponent(componentId, entityKey, config);
        }
    };
    
    private MockCustomizationAssemblyFactoryImpl customizationAssemblyFactory = new MockCustomizationAssemblyFactoryImpl();
    
    /**
     * Duplicate entity reference config
     * @author agrebneva
     *
     */
    @Configuration
    static class ContextConfigurationWithBadEntityRefSignature {
        @Bean
        public BadManagerWithWrongEntitySignature badManager() {
            return new BadManagerWithWrongEntitySignature();
        }
    }
    
    /**
     * Mock Manager with bad signature
     * @author agrebneva
     *
     */
    @Component
    public static class BadManagerWithWrongEntitySignature implements Manager {
        
        /**
         * Bad signature mapping
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("testBad")
        public GenericEntity getTest1(String token, Object studentId) {
            return simpleMaleStudentEntity;
        }
    }
    
    /**
     * Duplicate entity reference config
     * @author agrebneva
     *
     */
    @Configuration
    static class ContextConfigurationWithDuplicates {
        @Bean
        public BadManagerWithDuplicate badManager() {
            return new BadManagerWithDuplicate();
        }
    }

    
    /**
     * Mock Manager with bad signature
     * @author agrebneva
     *
     */
    @Component
    public static class BadManagerWithDuplicate implements Manager {
        
        /**
         * Good 
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("test")
        public GenericEntity getTest(String token, Object studentId, Config.Data data) {
            return simpleMaleStudentEntity;
        }
        
        /**
         * Bad signature mapping
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("test")
        public GenericEntity getTestDuplicate(String token, Object studentId, Config.Data data) {
            return simpleFemaleStudentEntity;
        }
    }
    
    /**
     * Duplicate entity reference config
     * @author agrebneva
     *
     */
    @Configuration
    static class ContextConfigurationWithGoodMananger {
        @Bean
        public GoodManager goodManager() {
            return new GoodManager();
        }
    }
    
    /**
     * Mock Manager with bad signature
     * @author agrebneva
     *
     */
    @Component
    public static class GoodManager implements Manager {
        
        /**
         * Bad signature mapping
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("test")
        public GenericEntity getTest1(String token, Object studentId, Config.Data config) {
            return simpleMaleStudentEntity;
        }
    }
    
    @BeforeClass
    public static void setupAll() {
        Gson gson = new GsonBuilder().create();
        configMap = new HashMap<String, Config>();
        configMap.put("studentProfile", gson.fromJson(DEFAULT_LAYOUT_JSON, Config.class));
        configMap.put("panel", gson.fromJson(DEFAULT_PANEL_JSON, Config.class));
        configMap.put("panel1", gson.fromJson(DEFAULT_PANEL1_JSON, Config.class));
        configMap.put("deep", gson.fromJson(DEFAULT_LAYOUT_TOO_DEEP_JSON, Config.class));
        
        simpleMaleStudentEntity = new GenericEntity();
        simpleMaleStudentEntity.put("id", "1");
        simpleMaleStudentEntity.put("gender", "male");
        simpleMaleStudentEntity.put("gradeNumeric", 5);
        simpleFemaleStudentEntity = new GenericEntity();
        simpleFemaleStudentEntity.put("id", "2");
        simpleFemaleStudentEntity.put("gender", "female");
        simpleFemaleStudentEntity.put("gradeNumeric", 7);
        
        sampleEntityMap = new HashMap<String, GenericEntity>();
        sampleEntityMap.put(simpleMaleStudentEntity.getString("id"), simpleMaleStudentEntity);
        sampleEntityMap.put(simpleFemaleStudentEntity.getString("id"), simpleFemaleStudentEntity);
    }
    
    @Before
    public void setup() {
        customizationAssemblyFactory.setApplicationContext(applicationContext);
    }
    
    /**
     * Test entity reference map contains student mapping
     */
    @Test
    public void testEntityReferenceMap() {
        Assert.assertTrue("Student entity reference must exists", customizationAssemblyFactory.hasCachedEntityMapperReference("student"));
        Assert.assertTrue(customizationAssemblyFactory.hasInvokableSet("student"));
    }
    
    /**
     * Test simple layout contains expected number of configs
     */
    @Test
    public void testSimpleLayout() {
        ModelAndViewConfig viewAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig("studentProfile", simpleMaleStudentEntity.get("id"));
        
        Assert.assertEquals(3, viewAndConfig.getComponentViewConfigMap().size());
    }
    
    /**
     * Test conditional layout contains different number of items depending on entity
     */
    @Test
    public void testConditionalLayout() {
        ModelAndViewConfig viewAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig("studentProfile", simpleMaleStudentEntity.get("id"));
        
        Assert.assertEquals(3, viewAndConfig.getComponentViewConfigMap().size());
        viewAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig("studentProfile", simpleFemaleStudentEntity.get("id"));
        Assert.assertEquals(1, viewAndConfig.getComponentViewConfigMap().size());
    }
    
    /**
     * Test not to allow infinite recursion
     */
    @Test
    public void testConfigTooDeep() {
        try {
            customizationAssemblyFactory.getModelAndViewConfig("deep", simpleMaleStudentEntity.get("id"));
        Assert.fail("Should not allow deep config structures");
        } catch (Throwable t) {
            Assert.assertEquals("The items hierarchy is too deep - only allow 5 elements", t.getMessage());
        }
    }
    
    /**
     * Test data domain condition
     */
    @Test
    public void testCondition() {
        Assert.assertTrue(
                "Must be true for a student with gender = 'male'", 
                customizationAssemblyFactory.checkCondition(configMap.get("panel"), simpleMaleStudentEntity));
        Assert.assertFalse(
                "Must be true for a student with gender = 'male'", 
                customizationAssemblyFactory.checkCondition(configMap.get("panel"), simpleFemaleStudentEntity));
        
        Assert.assertTrue(
                "Must be true for a student with gradeNumeric = 5", 
                customizationAssemblyFactory.checkCondition(configMap.get("panel1"), simpleMaleStudentEntity));
        Assert.assertFalse(
                "Must be false for a student with gradeNumeric = 7", 
                customizationAssemblyFactory.checkCondition(configMap.get("panel1"), simpleFemaleStudentEntity));
    }
    
    /**
     * Test not to allow bad signature entity references
     */
    @Test
    public void testBadEntityReference() {
        ApplicationContext badContext = new AnnotationConfigApplicationContext(new Class<?>[]{ContextConfigurationWithBadEntityRefSignature.class});
        try {
            customizationAssemblyFactory.setApplicationContext(badContext);
            Assert.fail("Should not allow testBad - bad signature");
        } catch (DashboardException de) {
            Assert.assertEquals("Wrong signature for the method for testBad. Expected is " 
                                + CustomizationAssemblyFactoryImpl.ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE.toString() + "!!!", de.getMessage());
            
        }
        
        badContext = new AnnotationConfigApplicationContext(new Class<?>[]{ContextConfigurationWithDuplicates.class});
        try {
            customizationAssemblyFactory.setApplicationContext(badContext);
            Assert.fail("Should not allow testBad - duplicate entity reference");
        } catch (DashboardException de) {
            Assert.assertEquals("Duplicate entity mapping references found for test. Fix!!!", de.getMessage());
            
        }
    }
    
    /**
     * Test getting a component for a test
     */
    @Test
    public void testDataComponent() {
        ApplicationContext goodContext = new AnnotationConfigApplicationContext(new Class<?>[]{ContextConfigurationWithGoodMananger.class});
        customizationAssemblyFactory.setApplicationContext(goodContext);
        // test good one
        Assert.assertEquals(
                simpleMaleStudentEntity, 
                customizationAssemblyFactory.getDataComponentForTest("panel", "1", configMap.get("panel").getData()));
        // test non-existent one
        Config.Data fakeDataConfig = null;
        try {
            fakeDataConfig = new Config.Data("fake", "fake", null);
            Assert.assertNotNull(fakeDataConfig);
            customizationAssemblyFactory.getDataComponentForTest("panel", "1", fakeDataConfig);
            Assert.fail("Must not be able to find fake entity reference and throw an exception");
        } catch (DashboardException de) {
            Assert.assertEquals("No entity mapping references found for " + fakeDataConfig.getEntityRef() + ". Fix!!!", de.getMessage());
        }
    }
}
