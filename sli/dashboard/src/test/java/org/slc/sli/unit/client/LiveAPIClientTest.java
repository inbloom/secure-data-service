package org.slc.sli.unit.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.client.RESTClient;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the Live API client.
 */
public class LiveAPIClientTest {

    private LiveAPIClient client;
    private RESTClient mockRest;

    @Before
    public void setUp() throws Exception {
        client = new LiveAPIClient();
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
        String url = Constants.API_SERVER_URI + "v1/students/1000/attendances";
        
        String json = "[{attendance: \"yes\"},{attendance:\"no\"}]";
        when(mockRest.makeJsonRequestWHeaders(url, null)).thenReturn(json);
        attendance = null;
        attendance = client.getStudentAttendance(null, "1000");
        assertNotNull(attendance);
        assert (attendance.size() == 2);
        assert (attendance.get(0).get("attendance").equals("yes"));
    }
}
