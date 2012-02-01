package org.slc.sli.unit.controller;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.gson.Gson;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.controller.StudentListController;
import org.slc.sli.entity.School;
import org.slc.sli.entity.EducationalOrganization;
import org.slc.sli.entity.EducationalOrganizationAssociation;
import org.slc.sli.entity.SchoolEducationalOrganizationAssociation;
import org.slc.sli.manager.InstitutionalHeirarchyManager;
import org.slc.sli.security.SLIPrincipal;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO: Write Javadoc
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StudentListController.class, MockAPIClient.class })
public class StudentListControllerTest {

    private StudentListController studentListController;
    private String testUser;
    
    @Before
    public void setup() {
        studentListController = new StudentListController();
        testUser = "sravan";
    }
    
    @Ignore
    @Test
    public void testStudentListNotEmpty() throws Exception {
        
        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());

        PowerMockito.doReturn("src/test/resources/mock_data/common/school.json").when(mockClient, "getFilename", "mock_data/common/school.json");
        School[] schools = mockClient.getSchools("common");
        PowerMockito.doReturn("src/test/resources/mock_data/common/educational_organization.json").when(mockClient, "getFilename", "mock_data/common/educational_organization.json");
        EducationalOrganization[] edorgs = mockClient.getEducationalOrganizations("common");
        PowerMockito.doReturn("src/test/resources/mock_data/common/school_educational_organization_association.json").when(mockClient, "getFilename", "mock_data/common/school_educational_organization_association.json");
        SchoolEducationalOrganizationAssociation[] schoolEdOrgAss = mockClient.getSchoolEducationalOrganizationAssociations("common");
        PowerMockito.doReturn("src/test/resources/mock_data/common/educational_organization_association.json").when(mockClient, "getFilename", "mock_data/common/educational_organization_association.json");
        EducationalOrganizationAssociation[] edOrgAss = mockClient.getEducationalOrganizationAssociations("common");
        
        ModelMap model = new ModelMap();
        StudentListController partiallyMocked = PowerMockito.spy(new StudentListController());
        InstitutionalHeirarchyManager schoolManager = PowerMockito.spy(new InstitutionalHeirarchyManager());
        PowerMockito.doReturn(schools).when(schoolManager, "getSchools");
        PowerMockito.doReturn(edorgs).when(schoolManager, "getEducationalOrganizations");
        PowerMockito.doReturn(schoolEdOrgAss).when(schoolManager, "getSchoolEducationalOrganizationAssociation");
        PowerMockito.doReturn(edOrgAss).when(schoolManager, "getEducationalOrganizationAssociations");
        SLIPrincipal principal = new SLIPrincipal("demo", "demo", "active");
        PowerMockito.doReturn(principal).when(partiallyMocked, "getPrincipal");
        
        ModelAndView result;   
        partiallyMocked.setInstitutionHeirarchyManager(schoolManager);
        result = partiallyMocked.retrieveStudentList(model);
        assertEquals(result.getViewName(), "studentList");
        String schoolListJson = (String) model.get("schoolList");
        Gson gson = new Gson();
        School[] retrievedList = gson.fromJson(schoolListJson, School[].class); 
        assertTrue(retrievedList.length > 0);
        int i = 0;
        for (School school : schools) {
            assertEquals(school.getNameOfInstitution(), retrievedList[i++].getNameOfInstitution());
        }

    }
    
    @Ignore
    @Test
    public void testStudentListNullReturn() throws Exception {
        StudentListController mocked = PowerMockito.spy(new StudentListController());
        InstitutionalHeirarchyManager schoolManager = PowerMockito.spy(new InstitutionalHeirarchyManager());
        PowerMockito.doReturn(null).when(schoolManager, "getSchools");
        PowerMockito.doReturn(null).when(schoolManager, "getEducationalOrganizations");
        PowerMockito.doReturn(null).when(schoolManager, "getSchoolEducationalOrganizationAssociation");
        PowerMockito.doReturn(null).when(schoolManager, "getEducationalOrganizationAssociations");
        mocked.setInstitutionHeirarchyManager(schoolManager);
        SLIPrincipal principal = new SLIPrincipal("demo", "demo", "active");
        PowerMockito.doReturn(principal).when(mocked, "getPrincipal");
        ModelMap model = new ModelMap();
        mocked.retrieveStudentList(model);
        assertTrue(model.get("schoolList").equals("null"));

    }
 
    /*
    @Test
    public void testApiClient() {
        Assert.assertNull(studentListController.getApiClient());
        MockAPIClient mock = new MockAPIClient();
        studentListController.setApiClient(mock);
        Assert.assertNotNull(studentListController.getApiClient());
        
        
    }
    */
}
