package org.slc.sli.unit.controller;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.controller.StudentListController;
import org.slc.sli.entity.GenericEntity;


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

    @Test
    public void testStudentListNotEmpty() throws Exception {

        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());

        PowerMockito.doReturn("src/test/resources/mock_data/common/school.json").when(mockClient, "getFilename", "mock_data/common/school.json");

        List<GenericEntity> schools = mockClient.getSchools("common", null);
        
        ModelMap model = new ModelMap();
        StudentListController partiallyMocked = PowerMockito.spy(new StudentListController());
        /*
        SchoolManager schoolManager = PowerMockito.spy(new SchoolManager());       
        PowerMockito.doReturn(schools).when(schoolManager, "getSchools");

        SLIPrincipal principal = new SLIPrincipal("demo", "demo", "active");
        PowerMockito.doReturn(principal).when(partiallyMocked, "getPrincipal");
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(principal, "common", new LinkedList<GrantedAuthority>()));

        ModelAndView result;
        partiallyMocked.setInstitutionalHierarchyManager(schoolManager);
        result = partiallyMocked.retrieveStudentList(model);
        assertEquals(result.getViewName(), "studentList");
        String instHierarchyJSONString = (String) model.get(StudentListController.INST_HEIRARCHY);
        JSONArray instHierarchyJSON = new JSONArray(instHierarchyJSONString);
        assertTrue(instHierarchyJSON.length() == 1);
        JSONObject edOrgJSON = instHierarchyJSON.getJSONObject(0);
        JSONArray schoolsJSONArray = edOrgJSON.getJSONArray(InstitutionalHierarchyManager.SCHOOLS);
        Gson gson = new Gson();
        School[] retrievedList = gson.fromJson(schoolsJSONArray.toString(), School[].class);
        assertTrue(retrievedList.length > 0);
        int i = 0;
        for (School school : schools) {
            assertEquals(school.getNameOfInstitution(), retrievedList[i++].getNameOfInstitution());
        }
*/
    }

    @Test
    public void testStudentListNullReturn() throws Exception {
        /*
        StudentListController mocked = PowerMockito.spy(new StudentListController());
        InstitutionalHierarchyManager schoolManager = PowerMockito.spy(new InstitutionalHierarchyManager());
        PowerMockito.doReturn(null).when(schoolManager, "getSchools", Matchers.anyObject());
        mocked.setInstitutionalHierarchyManager(schoolManager);
        SLIPrincipal principal = new SLIPrincipal("demo", "demo", "active");
        PowerMockito.doReturn(principal).when(mocked, "getPrincipal");
        ModelMap model = new ModelMap();
        mocked.retrieveStudentList(model);
        String instHierarchyJSONString = (String) model.get(StudentListController.INST_HEIRARCHY);
        JSONArray instHierarchyJSON = new JSONArray(instHierarchyJSONString);
        assertTrue(instHierarchyJSON.length() == 0);
*/
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
