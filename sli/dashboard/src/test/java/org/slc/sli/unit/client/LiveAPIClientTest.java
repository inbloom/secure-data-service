package org.slc.sli.unit.client;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.client.RESTClient;
import org.slc.sli.entity.GenericEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test for the Live API client.
 */
public class LiveAPIClientTest {

    private LiveAPIClient client;
    private RESTClient mockRest;
    private ApplicationContext appContext;

    @Before
    public void setUp() throws Exception {
        // Get the initalized bean from spring config
        appContext = new ClassPathXmlApplicationContext("application-context.xml");
        client = (LiveAPIClient) appContext.getBean("apiClient");
        mockRest = mock(RESTClient.class);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        mockRest = null;
    }

    @Test
    public void testGetStudentAttendance() throws Exception {
        List<GenericEntity> attendance;
        attendance = client.getStudentAttendance(null, null);
        assertNotNull(attendance);
        assert (attendance.size() == 0);
        
        String url = client.getApiUrl() + "v1/students/1000/attendances";
        
        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000");
        assertNotNull(attendance);
        assert (attendance.size() == 2);
        assert (attendance.get(0).get("attendance").equals("yes"));
    }
}
