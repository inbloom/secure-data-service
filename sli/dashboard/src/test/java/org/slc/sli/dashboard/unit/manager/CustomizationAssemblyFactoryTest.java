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

package org.slc.sli.dashboard.unit.manager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Item;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.ModelAndViewConfig;
import org.slc.sli.dashboard.manager.Manager.EntityMapping;
import org.slc.sli.dashboard.manager.Manager.EntityMappingManager;
import org.slc.sli.dashboard.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test CustomizationAssemblyFactory features
 * 
 * @author agrebneva
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class CustomizationAssemblyFactoryTest {
    @Autowired
    ApplicationContext applicationContext;
    
    private static final String DEFAULT_LAYOUT_JSON = "{id : 'studentProfile', type: 'LAYOUT', "
            + " data :{entity: 'mock', alias: 'mock' }, "
            + " items: [{id : 'panel', name: 'Student Info', type: 'PANEL'},"
            + "         {id: 'tab2', condition: {field: 'gender', value: ['male']}, name: 'Attendance and Discipline', type : 'TAB', "
            + "          items: [{id : 'panel', type: 'PANEL'}]}]}";
    private static final String DEFAULT_LAYOUT_TOO_DEEP_JSON = "{id : 'deep', type: 'LAYOUT', "
            + " data :{entity: 'mock', alias: 'mock' }, "
            + " items: [{id : 'deep', name: 'Student Info', type: 'PANEL'}]}";
    private static final String DEFAULT_PANEL_JSON = "{id : 'panel', type: 'PANEL', condition: {field: 'gender', value: ['male']}, data :{entity: 'test',alias: 'mock' }}";
    
    private static final String DEFAULT_PANEL1_JSON = "{id : 'panel1', type: 'PANEL', condition: {field: 'gradeNumeric', value: [1,2,5]}, data :{entity: 'test',alias: 'mock' }}";
    
    private static final String DEFAULT_PANEL_EXCEPTION_JSON = "{id : 'panelException', type: 'PANEL', condition: {field: 'gradeNumeric', value: [1,2,5]}, data :{entity: 'testException',alias: 'mock' }}";
    
    private static final String PANEL_WITH_DYNAMIC_HEADERS = "{id : 'dynamicHeaders', type: 'PANEL', items: [{name: '${meta.name.first}'}, {name: '${meta.id}'}]}";
    
    private static GenericEntity simpleMaleStudentEntity;
    private static GenericEntity simpleFemaleStudentEntity;
    private static GenericEntity simpleCacheData;
    
    private static Map<String, Config> configMap;
    
    private static Map<String, GenericEntity> sampleEntityMap;
    
    private static GenericEntity simpleNoGenderInfoStudentEntity;
    
    private static final String CACHE_TEST_JSON = "{\"id\":\"section\",\"parentId\":\"section\",\"name\":\"SLC - Section Profile\",\"type\":\"LAYOUT\",\"data\":{\"entity\":\"sectionInfo\",\"cacheKey\":\"sectionInfo\",\"lazy\":true},\"items\":[{\"id\":\"tab3\",\"parentId\":\"tab3\",\"name\":\"List of Students\",\"type\":\"TAB\",\"items\":[{\"id\":\"listOfStudents\",\"parentId\":\"listOfStudents\",\"type\":\"PANEL\"}]},{\"id\":\"tab4\",\"name\":\"LOS\",\"type\":\"TAB\",\"items\":[{\"id\":\"listOfStudents\",\"parentId\":\"listOfStudents\",\"type\":\"PANEL\"}]}]}";
    private static final String LIST_OF_STUDENTS_JSON = "{ id : \"listOfStudents\", type : \"PANEL\", data :{ lazy: true, entity: \"listOfStudents\", cacheKey: \"listOfStudents\" }, root: 'students', items : [ {name: \"Default View\", items: [ {name: \"Student\", width: 150, field: \"name.fullName\", formatter:restLink, style:'ui-ellipsis', params: {link:'student', target:\"_self\"}}, {name: \"\", width: 60, field: \"programParticipation\", formatter: Lozenge}, {name: \"Grade\", field: \"score.grade\", width:50, formatter: TearDrop}, {name: \"Absence Count\", field: \"attendances.absenceCount\", width:100, sorter: 'int', formatter: CutPointReverse, params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style:'color-widget-red'}}}}, {name: \"Tardy Count\", field: \"attendances.tardyCount\", width:100, sorter: 'int', formatter: CutPointReverse, params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style:'color-widget-red'}}}} ] } ] }";
    private static final String LIST_OF_STUDENTS_ENTITY = "{\"students\":[{\"studentGradebookEntries\":[],\"sex\":\"Male\",\"studentCharacteristics\":[],\"hispanicLatinoEthnicity\":false,\"disabilities\":[],\"cohortYears\":[],\"section504Disabilities\":[],\"studentSectionAssociation\":[{\"id\":\"2012rh-0c7659b7-e000-11e1-9f3b-3c07546832b4\",\"sectionId\":\"2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4\",\"studentId\":\"2012zv-0665ebcb-e000-11e1-9f3b-3c07546832b4\",\"entityType\":\"studentSectionAssociation\"}],\"race\":[],\"programParticipations\":[],\"id\":\"2012zv-0665ebcb-e000-11e1-9f3b-3c07546832b4\",\"studentUniqueStateId\":\"800000025\",\"languages\":[],\"attendances\":{\"tardyRate\":0,\"attendanceRate\":95,\"tardyCount\":0,\"absenceCount\":4},\"name\":{\"middleName\":\"Joseph\",\"generationCodeSuffix\":\"Jr\",\"lastSurname\":\"Sollars\",\"fullName\":\"Matt Sollars\",\"firstName\":\"Matt\"},\"birthData\":{\"birthDate\":\"2000-04-23\"},\"otherName\":[],\"studentIndicators\":[],\"homeLanguages\":[],\"limitedEnglishProficiency\":\"Limited\",\"studentIdentificationCode\":[],\"address\":[],\"electronicMail\":[{\"emailAddress\":\"m.sollars@gmail.com\",\"emailAddressType\":\"Other\"}],\"gradeLevel\":\"Eighth grade\",\"schoolId\":\"2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\",\"telephone\":[{\"telephoneNumber\":\"309-555-2449\",\"primaryTelephoneNumberIndicator\":true,\"telephoneNumberType\":\"Home\"}],\"previousSemester\":[{\"letterGrade\":\"A-\",\"courseTitle\":\"ELA 6A\"}]}]}";
    private static final String SECTION_ENTITY = "{\"id\":\"2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4\",\"sessionId\":\"2012yw-0b7a6d39-e000-11e1-9f3b-3c07546832b4\",\"courseOfferingId\":\"2012wq-0ba85b67-e000-11e1-9f3b-3c07546832b4\",\"populationServed\":\"Regular Students\",\"sequenceOfCourse\":3,\"uniqueSectionCode\":\"6th Grade English - Sec 4\",\"mediumOfInstruction\":\"Independent study\",\"programReference\":[],\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getSchool\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getEducationOrganization\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSession\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sessions/2012yw-0b7a6d39-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getCourseOffering\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/courseOfferings/2012wq-0ba85b67-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getGradebookEntries\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/gradebookEntries?sectionId\u003d2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getStudentGradebookEntries\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/studentGradebookEntries?sectionId\u003d2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getStudentSectionAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4/studentSectionAssociations\"},{\"linkName\":\"getStudents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4/studentSectionAssociations/students\"},{\"linkName\":\"getTeacherSectionAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4/teacherSectionAssociations\"},{\"linkName\":\"getTeachers\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections/2012pd-0bb098cf-e000-11e1-9f3b-3c07546832b4/teacherSectionAssociations/teachers\"}],\"schoolId\":\"2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\",\"sectionName\":\"6th Grade English - Sec 4\",\"teacherName\":{\"verification\":\"Life insurance policy\",\"lastSurname\":\"Kim\",\"personalTitlePrefix\":\"Mrs\",\"firstName\":\"Linda\"},\"courseTitle\":\"6th Grade English\",\"subjectArea\":\"English Language and Literature\"}";
    
    /**
     * Expose some methods
     * 
     * @author agrebneva
     * 
     */
    class MockCustomizationAssemblyFactoryImpl extends CustomizationAssemblyFactoryImpl {
        
        @Override
        protected String getTokenId() {
            return "1";
        }
        
        @Override
        protected Config getConfig(String componentId) {
            
            return configMap.get(componentId);
        }
        
        @Override
        public Collection<Config> getWidgetConfigs() {
            return Collections.emptyList();
        }
        
        @Override
        public GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            return sampleEntityMap.get(entityKey);
        }
        
        public boolean hasInvokableSet(String entityRef) {
            return getInvokableSet(entityRef) != null;
        }
        
        public GenericEntity getDataComponentForTest(String componentId, Object entityKey, Config.Data config) {
            return super.getDataComponent(componentId, entityKey, config);
        }
        
        @Override
        protected Config.Item[] getUpdatedDynamicHeaderTemplate(Config config, GenericEntity entity) {
            return super.getUpdatedDynamicHeaderTemplate(config, entity);
        }
    };
    
    private MockCustomizationAssemblyFactoryImpl customizationAssemblyFactory = new MockCustomizationAssemblyFactoryImpl();
    
    /**
     * Duplicate entity reference config
     * 
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
     * 
     * @author agrebneva
     * 
     */
    @Component
    @EntityMappingManager
    public static class BadManagerWithWrongEntitySignature {
        
        /**
         * Bad signature mapping
         * 
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
     * 
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
     * 
     * @author agrebneva
     * 
     */
    @Component
    @EntityMappingManager
    public static class BadManagerWithDuplicate {
        
        /**
         * Good
         * 
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
         * 
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
     * 
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
     * 
     * @author agrebneva
     * 
     */
    @Component
    @EntityMappingManager
    public static class GoodManager {
        
        /**
         * Good signature mapping
         * 
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("test")
        public GenericEntity getTest1(String token, Object studentId, Config.Data config) {
            return simpleMaleStudentEntity;
        }
        
        /**
         * Good signature mapping but throws an exception
         * 
         * @param token
         * @param studentId
         * @param config
         * @return
         */
        @EntityMapping("testException")
        public GenericEntity getTestException(String token, Object studentId, Config.Data config) throws Exception {
            throw new Exception("Something bad happened");
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
        configMap.put("panelException", gson.fromJson(DEFAULT_PANEL_EXCEPTION_JSON, Config.class));
        configMap.put("dynamicHeaders", gson.fromJson(PANEL_WITH_DYNAMIC_HEADERS, Config.class));
        configMap.put("test", gson.fromJson(CACHE_TEST_JSON, Config.class));
        configMap.put("listOfStudents", gson.fromJson(LIST_OF_STUDENTS_JSON, Config.class));
        
        simpleMaleStudentEntity = new GenericEntity();
        simpleMaleStudentEntity.put("id", "1");
        simpleMaleStudentEntity.put("gender", "male");
        simpleMaleStudentEntity.put("gradeNumeric", 5);
        simpleFemaleStudentEntity = new GenericEntity();
        simpleFemaleStudentEntity.put("id", "2");
        simpleFemaleStudentEntity.put("gender", "female");
        simpleFemaleStudentEntity.put("gradeNumeric", 7);
        
        simpleNoGenderInfoStudentEntity = new GenericEntity();
        simpleNoGenderInfoStudentEntity.put("id", "3");
        simpleNoGenderInfoStudentEntity.put("gradeNumeric", 7);
        
        simpleCacheData = new GenericEntity();
        simpleCacheData.put("id", "testEntityKey");
        simpleCacheData.put("data", "testData1");
        
        sampleEntityMap = new HashMap<String, GenericEntity>();
        sampleEntityMap.put(simpleMaleStudentEntity.getString("id"), simpleMaleStudentEntity);
        sampleEntityMap.put(simpleFemaleStudentEntity.getString("id"), simpleFemaleStudentEntity);
        sampleEntityMap.put(simpleNoGenderInfoStudentEntity.getString("id"), simpleNoGenderInfoStudentEntity);
        sampleEntityMap.put(simpleCacheData.getString("id"), simpleCacheData);
        sampleEntityMap.put("section", JsonConverter.fromJson(SECTION_ENTITY, GenericEntity.class));
        sampleEntityMap.put("listOfStudents", JsonConverter.fromJson(LIST_OF_STUDENTS_ENTITY, GenericEntity.class));
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
        Assert.assertTrue("Student entity reference must exists",
                customizationAssemblyFactory.hasCachedEntityMapperReference("student"));
        Assert.assertTrue(customizationAssemblyFactory.hasInvokableSet("student"));
    }
    
    /**
     * Test simple layout contains expected number of configs
     */
    @Test
    public void testSimpleLayout() {
        ModelAndViewConfig viewAndConfig = customizationAssemblyFactory.getModelAndViewConfig("studentProfile",
                simpleMaleStudentEntity.get("id"));
        
        Assert.assertEquals(3, viewAndConfig.getConfig().size());
    }
    
    /**
     * Test conditional layout contains different number of items depending on entity
     */
    @Test
    public void testConditionalLayout() {
        ModelAndViewConfig viewAndConfig = customizationAssemblyFactory.getModelAndViewConfig("studentProfile",
                simpleMaleStudentEntity.get("id"));
        
        Assert.assertEquals(3, viewAndConfig.getConfig().size());
        viewAndConfig = customizationAssemblyFactory.getModelAndViewConfig("studentProfile",
                simpleFemaleStudentEntity.get("id"));
        Assert.assertEquals(1, viewAndConfig.getConfig().size());
    }
    
    /**
     * Test not to allow infinite recursion
     */
    @Test
    public void testConfigTooDeep() {
        try {
            customizationAssemblyFactory.getModelAndViewConfig("deep", simpleMaleStudentEntity.get("id"));
            Assert.fail("Should not allow deep config structures");
        } catch (DashboardException de) {
            Assert.assertEquals("The items hierarchy is too deep - only allow 5 elements", de.getMessage());
        }
    }
    
    /**
     * Test data domain condition
     */
    @Test
    public void testCondition() {
        Config panel = configMap.get("panel");
        Assert.assertTrue("Must be true for a student with gender = 'male'",
                customizationAssemblyFactory.checkCondition(panel, panel, simpleMaleStudentEntity));
        Assert.assertFalse("Must be true for a student with gender = 'male'",
                customizationAssemblyFactory.checkCondition(panel, panel, simpleFemaleStudentEntity));
        
        Assert.assertFalse("Must be false for a student with no gender field available",
                customizationAssemblyFactory.checkCondition(panel, panel, simpleNoGenderInfoStudentEntity));
        
        panel = configMap.get("panel1");
        Assert.assertTrue("Must be true for a student with gradeNumeric = 5",
                customizationAssemblyFactory.checkCondition(panel, panel, simpleMaleStudentEntity));
        Assert.assertFalse("Must be false for a student with gradeNumeric = 7",
                customizationAssemblyFactory.checkCondition(panel, panel, simpleFemaleStudentEntity));
        
    }
    
    /**
     * Test not to allow bad signature entity references
     */
    @Test
    public void testBadEntityReference() {
        ApplicationContext badContext = new AnnotationConfigApplicationContext(
                new Class<?>[] { ContextConfigurationWithBadEntityRefSignature.class });
        try {
            customizationAssemblyFactory.setApplicationContext(badContext);
            Assert.fail("Should not allow testBad - bad signature");
        } catch (DashboardException de) {
            Assert.assertEquals(
                    "Wrong signature for the method for testBad. Expected is "
                            + Arrays.asList(CustomizationAssemblyFactoryImpl.ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE)
                            + "!!!", de.getMessage());
            
        }
        
        badContext = new AnnotationConfigApplicationContext(new Class<?>[] { ContextConfigurationWithDuplicates.class });
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
        ApplicationContext goodContext = new AnnotationConfigApplicationContext(
                new Class<?>[] { ContextConfigurationWithGoodMananger.class });
        customizationAssemblyFactory.setApplicationContext(goodContext);
        // test good one
        Assert.assertEquals(simpleMaleStudentEntity,
                customizationAssemblyFactory.getDataComponentForTest("panel", "1", configMap.get("panel").getData()));
        // test an entity mapping that throws an exception
        Assert.assertNull(customizationAssemblyFactory.getDataComponentForTest("panelException", "1",
                configMap.get("panelException").getData()));
        
        // test non-existent one
        Config.Data fakeDataConfig = new Config.Data("fake", "fake", false, null);
        Assert.assertNotNull(fakeDataConfig);
        try {
            customizationAssemblyFactory.getDataComponentForTest("panel", "1", fakeDataConfig);
            Assert.fail("Must not be able to find fake entity reference and throw an exception");
        } catch (DashboardException de) {
            Assert.assertEquals("No entity mapping references found for " + fakeDataConfig.getEntityRef() + ". Fix!!!",
                    de.getMessage());
        }
    }
    
    @Test
    public void testDynamicHeaders() {
        Config panel = configMap.get("dynamicHeaders");
        GenericEntity meta = new GenericEntity();
        meta.put("id", "Funky ID");
        GenericEntity name = new GenericEntity();
        name.put("first", "AAA");
        meta.put("name", name);
        GenericEntity entity = new GenericEntity();
        entity.put("meta", meta);
        Config.Item[] items = customizationAssemblyFactory.getUpdatedDynamicHeaderTemplate(panel, entity);
        Assert.assertEquals("AAA", items[0].getName());
        Assert.assertEquals("Funky ID", items[1].getName());
    }
    
    @Test
    public void testCache() {
        ApplicationContext goodContext = new AnnotationConfigApplicationContext(
                new Class<?>[] { ContextConfigurationWithGoodMananger.class });
        customizationAssemblyFactory.setApplicationContext(goodContext);
        ModelAndViewConfig modelAndViewConfig = customizationAssemblyFactory.getModelAndViewConfig("test", "section",
                true);
        Config testConfig = modelAndViewConfig.getConfig().get("test");
        Assert.assertEquals("section", testConfig.getId());
        Assert.assertEquals("section", testConfig.getParentId());
        Assert.assertEquals("SLC - Section Profile", testConfig.getName());
        Assert.assertEquals("LAYOUT", testConfig.getType().toString());
        Item[] items = testConfig.getItems();
        Assert.assertEquals(2, items.length);
        Assert.assertEquals("tab3", items[0].getId());
        Assert.assertEquals("List of Students", items[0].getName());
        Item[] itemA = items[0].getItems();
        Assert.assertEquals(1, itemA.length);
        Assert.assertEquals("listOfStudents", itemA[0].getId());
        Item[] itemAA = itemA[0].getItems();
        Assert.assertEquals(1, itemAA.length);
        Assert.assertEquals("Default View", itemAA[0].getName());
        Item[] itemAAA = itemAA[0].getItems();
        Assert.assertEquals(5, itemAAA.length);
        Assert.assertEquals("Student", itemAAA[0].getName());
        Assert.assertEquals("", itemAAA[1].getName());
        Assert.assertEquals("Grade", itemAAA[2].getName());
        Assert.assertEquals("Absence Count", itemAAA[3].getName());
        Assert.assertEquals("Tardy Count", itemAAA[4].getName());
        
        Assert.assertEquals("tab4", items[1].getId());
        Assert.assertEquals("LOS", items[1].getName());
        Item[] itemB = items[1].getItems();
        Assert.assertEquals(1, itemB.length);
        Assert.assertEquals("listOfStudents", itemB[0].getId());
        Item[] itemBA = itemB[0].getItems();
        Assert.assertEquals(1, itemBA.length);
        Assert.assertEquals("Default View", itemBA[0].getName());
        Item[] itemBAA = itemBA[0].getItems();
        Assert.assertEquals(5, itemBAA.length);
        Assert.assertEquals("Student", itemBAA[0].getName());
        Assert.assertEquals("", itemBAA[1].getName());
        Assert.assertEquals("Grade", itemBAA[2].getName());
        Assert.assertEquals("Absence Count", itemBAA[3].getName());
        Assert.assertEquals("Tardy Count", itemBAA[4].getName());
    }
}
