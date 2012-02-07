package org.slc.sli.unit.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.manager.InstitutionalHeirarchyManager;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Unit tests for the InstitutionalHeirarchyManager class.
 *
 */
public class InstitutionalHeirarchyManagerTest {

    @Before
    public void setup() {
    }

    @Test
    public void testGetInstHeirarhy() throws Exception {

        InstitutionalHeirarchyManager iManager = new InstitutionalHeirarchyManager(); 

        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        when(mockClient.getFilename("mock_data/cgray/school.json")).thenReturn("src/test/resources/mock_data/cgray/school.json");
        when(mockClient.getFilename("mock_data/cgray/school_educational_organization_association.json")).thenReturn("src/test/resources/mock_data/cgray/school_educational_organization_association.json");
        when(mockClient.getFilename("mock_data/cgray/educational_organization_association.json")).thenReturn("src/test/resources/mock_data/cgray/educational_organization_association.json");
        when(mockClient.getFilename("mock_data/cgray/educational_organization.json")).thenReturn("src/test/resources/mock_data/cgray/educational_organization.json");
        iManager.setApiClient(mockClient);
        String json = iManager.getInstHeirarchyJSON("cgray");

        JSONArray instArray = new JSONArray(json);
        assertEquals(3, instArray.length());

        // Check that there is one school in Sunset, one school in Daybreak, and two schools in IL
        JSONObject sunsetJSON = null;
        JSONObject daybreakJSON = null;
        JSONObject ilJSON = null;
        for (int i = 0; i < instArray.length(); i++) {
            JSONObject obj = instArray.getJSONObject(i);
            if ("Sunset School District 4526".equals(obj.getString("name"))) {
                sunsetJSON = obj;
            } else if ("Daybreak School District 4529".equals(obj.getString("name"))) {
                daybreakJSON = obj;
            } else if ("Illinois State Board of Education".equals(obj.getString("name"))) {
                ilJSON = obj;
            }
        }
        assertNotNull(sunsetJSON);
        assertNotNull(daybreakJSON);
        assertNotNull(ilJSON);
        assertEquals(1, sunsetJSON.getJSONArray("schools").length());
        assertEquals(1, daybreakJSON.getJSONArray("schools").length());
        assertEquals(2, ilJSON.getJSONArray("schools").length());
    }

}
