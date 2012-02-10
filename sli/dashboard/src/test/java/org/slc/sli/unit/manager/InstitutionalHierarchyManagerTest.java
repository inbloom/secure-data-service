package org.slc.sli.unit.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.InstitutionalHierarchyManager;

/**
 * Unit tests for the InstitutionalHierarchyManager class.
 *
 */
public class InstitutionalHierarchyManagerTest {

    @Before
    public void setup() {
    }

    @Test
    public void testGetInstHierarhy() throws Exception {

        InstitutionalHierarchyManager iManager = new InstitutionalHierarchyManager(); 

        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        when(mockClient.getFilename("mock_data/cgray/school.json")).thenReturn("src/test/resources/mock_data/cgray/school.json");
        when(mockClient.getFilename("mock_data/cgray/school_educational_organization_association.json")).thenReturn("src/test/resources/mock_data/cgray/school_educational_organization_association.json");
        when(mockClient.getFilename("mock_data/cgray/educational_organization_association.json")).thenReturn("src/test/resources/mock_data/cgray/educational_organization_association.json");
        when(mockClient.getFilename("mock_data/cgray/educational_organization.json")).thenReturn("src/test/resources/mock_data/cgray/educational_organization.json");
        iManager.setApiClient(mockClient);
        List<GenericEntity> instArray = iManager.getInstHierarchy("cgray");

        assertEquals(3, instArray.size());

        // Check that there is one school in Sunset, one school in Daybreak, and two schools in IL
        GenericEntity sunset = null;
        GenericEntity daybreak = null;
        GenericEntity il = null;
        for (int i = 0; i < instArray.size(); i++) {
            GenericEntity obj = instArray.get(i);
            if ("Sunset School District 4526".equals(obj.get("name"))) {
                sunset = obj;
            } else if ("Daybreak School District 4529".equals(obj.get("name"))) {
                daybreak = obj;
            } else if ("Illinois State Board of Education".equals(obj.get("name"))) {
                il = obj;
            }
        }
        assertNotNull(sunset);
        assertNotNull(daybreak);
        assertNotNull(il);
        assertEquals(1, ((Set) (sunset.get("schools"))).size());
        assertEquals(1, ((Set) (daybreak.get("schools"))).size());
        assertEquals(2, ((Set) (il.get("schools"))).size());
    }

}
