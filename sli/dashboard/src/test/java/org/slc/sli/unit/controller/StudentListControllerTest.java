package org.slc.sli.unit.controller;


import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slc.sli.client.MockAPIClient;
import org.slc.sli.controller.StudentListController;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.InstitutionalHierarchyManager;
import org.slc.sli.manager.impl.InstitutionalHierarchyManagerImpl;
import org.slc.sli.security.SLIPrincipal;
import org.slc.sli.util.SecurityUtil;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


/**
 * TODO: Write Javadoc
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StudentListController.class, MockAPIClient.class, SecurityUtil.class })
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
        StudentListController studentListController = PowerMockito.spy(new StudentListController());
        
        InstitutionalHierarchyManager mockedInstManager = PowerMockito.spy(new InstitutionalHierarchyManagerImpl());
        
        PowerMockito.doReturn(new LinkedList<GenericEntity>()).when(mockedInstManager, "getUserInstHierarchy", "fakeToken");
        PowerMockito.mockStatic(SecurityUtil.class);

        SLIPrincipal fakeUser = new SLIPrincipal();
        fakeUser.setName("fake");
        PowerMockito.mockStatic(SecurityUtil.class);
        Mockito.when(SecurityUtil.getToken()).thenReturn("fakeToken");        
        Mockito.when(SecurityUtil.getPrincipal()).thenReturn(fakeUser);

        studentListController.setInstitutionalHierarchyManager(mockedInstManager);
        ModelAndView mav = studentListController.retrieveStudentList(model);
        assertEquals("fake", model.get("username"));
        assertEquals("[]", model.get("instHierarchy"));
    }

}
