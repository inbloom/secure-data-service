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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.SDKAPIClient;
import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.EntityManager;
import org.slc.sli.dashboard.manager.impl.ConfigManagerImpl;
import org.slc.sli.dashboard.security.SLIPrincipal;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.JsonConverter;

/**
 * Unit tests for the StudentManager class.
 *
 */
public class ConfigManagerTest {

    ConfigManagerImpl configManager;
    APIClient apiClient = null;

    @Before
    public void setup() {
        apiClient = new SDKAPIClient() {
            @Override
            public GenericEntity getEducationalOrganization(String token, String id) {
                GenericEntity entity = null;
                if (token.equals("1") && id.equals("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4")) {
                    String json = "{\"id\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"organizationCategories\":[\"Local Education Agency\"],\"address\":[{\"postalCode\":\"00000\",\"streetNumberName\":\"123 Street\",\"stateAbbreviation\":\"IL\",\"city\":\"Chicago\"}],\"educationOrgIdentificationCode\":[],\"programReference\":[],\"LEACategory\":\"Independent\",\"parentEducationAgencyReference\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getParentEducationOrganization\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getFeederSchools\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/schools?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getStaffEducationOrgAssignmentAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"linkName\":\"getStaff\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/staff\"},{\"linkName\":\"getCourses\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getFeederEducationOrganizations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSections\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getAttendances\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getCohorts\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSessions\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getDisciplineIncidents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"}],\"stateOrganizationId\":\"IL-DAYBREAK\",\"telephone\":[],\"nameOfInstitution\":\"Daybreak School District 4529\"}";
                    entity = JsonConverter.fromJson(json, GenericEntity.class);
                }
                return entity;
            }

            @Override
            public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrg) {
                GenericEntity entity = null;
                if (token.equals("1") && edOrg.getId().equals("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4")) {
                    String json = "{\"id\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"organizationCategories\":[\"State Education Agency\"],\"address\":[{\"postalCode\":\"00000\",\"streetNumberName\":\"123 Street\",\"stateAbbreviation\":\"IL\",\"city\":\"Chicago\"}],\"educationOrgIdentificationCode\":[],\"stateOrganizationId\":\"IL\",\"programReference\":[],\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getFeederSchools\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/schools?parentEducationAgencyReference\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getStaffEducationOrgAssignmentAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"linkName\":\"getStaff\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/staff\"},{\"linkName\":\"getCourses\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getFeederEducationOrganizations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations?parentEducationAgencyReference\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSections\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getAttendances\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getCohorts\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSessions\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getDisciplineIncidents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"}],\"telephone\":[],\"nameOfInstitution\":\"Illinois State Board of Education\"}";
                    entity = JsonConverter.fromJson(json, GenericEntity.class);
                }
                return entity;
            }

            @Override
            public ConfigMap getEdOrgCustomData(String token, String id) {
                ConfigMap configMap = null;
                if (id.equals("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4")) {
                    String json = "{\"config\":{\"school\":{\"id\":\"school\",\"parentId\":\"school\",\"name\":\"SLC - School Profile\",\"type\":\"LAYOUT\",\"data\":{\"entity\":\"schoolInfo\",\"cacheKey\":\"schoolInfo\",\"lazy\":false},\"items\":[{\"id\":\"populationWidget\",\"parentId\":\"populationWidget\",\"name\":\"Organizational hierarchy drop down menu\",\"type\":\"PANEL\"},{\"id\":\"schoolInfo\",\"parentId\":\"schoolInfo\",\"name\":\"School Info\",\"type\":\"PANEL\"},{\"id\":\"tab1\",\"parentId\":\"tab1\",\"name\":\"Subjects and Courses\",\"type\":\"TAB\",\"items\":[{\"id\":\"sectionList\",\"parentId\":\"sectionList\",\"type\":\"TREE\"}]},{\"id\":\"tab2\",\"parentId\":\"tab2\",\"name\":\"Teachers\",\"type\":\"TAB\",\"items\":[{\"id\":\"teacherList\",\"parentId\":\"teacherList\",\"type\":\"GRID\"}]},{\"id\":\"tab3\",\"parentId\":\"tab3\",\"name\":\"rrogers\",\"type\":\"TAB\",\"items\":[]},{\"id\":\"tab4\",\"name\":\"jstevenson\",\"type\":\"TAB\",\"items\":[]}]}}}";
                    configMap = JsonConverter.fromJson(json, ConfigMap.class);
                }
                return configMap;
            }
        };
        configManager = new ConfigManagerImpl() {
            @Override
            public ConfigMap getCustomConfig(String token, EdOrgKey edOrgKey) {
                return null;
            }
        };
        configManager.setDriverConfigLocation("config");
        configManager.setApiClient(apiClient);

        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(apiClient);
        SLIPrincipal principal = new SLIPrincipal();
        principal.setDistrict("test_district");
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(principal, null));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Test get config to return expected
     */
    @Test
    public void testConfigFields() {
        Config config = configManager.getComponentConfig("1", new EdOrgKey(
                "2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4"), "gridSample");
        Assert.assertEquals("gridSample", config.getId());
        Assert.assertEquals("attendance", config.getRoot());
        Assert.assertEquals(config.getName(), "Grid");
        Assert.assertEquals("studentAttendance", config.getData().getEntityRef());
        Assert.assertEquals("studentAttendance", config.getData().getCacheKey());
        Config.Item[] items = config.getItems();
        Assert.assertEquals(items.length, 3);
        // check the default one is a FIELD
        Assert.assertEquals(Config.Type.FIELD, items[0].getType());
        Assert.assertEquals("eventDate", items[0].getField());
        Assert.assertEquals("string", items[0].getDatatype());
        Assert.assertEquals("Month", items[0].getName());
        // check various fields
        Assert.assertEquals("PercentBarFormatter", items[1].getFormatter());
        Assert.assertEquals("90", items[0].getWidth());
        Assert.assertEquals(2, items[1].getParams().size());
        Assert.assertEquals(2, items[2].getItems().length);
        Assert.assertEquals("float", items[1].getSorter());
        // test condition
        Config.Condition condition = items[1].getCondition();
        Assert.assertNotNull(condition);
        Assert.assertEquals("x", condition.getField());
        Assert.assertEquals(3, condition.getValue().length);
        //
        Assert.assertEquals("Condition [field=x, value=[x, y, z]]", condition.toString());
        Assert.assertEquals("ViewItem [width=90, type=string, color=null, style=null, formatter=null, params=null]",
                items[0].toString());
    }

    @Test
    public void testNonexistentConfig() {
        try {
            configManager.getComponentConfig("1", new EdOrgKey("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4"),
                    "fakeConfigId");
            fail();
        } catch (DashboardException de) {
            assertTrue(de.getMessage().contains("Unable to read config for fakeConfigId"));
        }
        
    }

    @Test
    public void testConfigLocation() {
        String location = "config";
        configManager.setDriverConfigLocation(location);
        Assert.assertTrue(configManager.getDriverConfigLocation("x").contains(location));
    }
}
