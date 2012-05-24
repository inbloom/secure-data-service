package org.slc.sli.unit.manager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.MockAPIClient;
import org.slc.sli.manager.impl.ConfigManagerImpl;
import org.slc.sli.security.SLIPrincipal;

/**
 * Unit tests for the StudentManager class.
 *
 */
public class ConfigManagerTest {

    ConfigManagerImpl configManager;
    MockAPIClient mockClient;

    @Before
    public void setup() {
        mockClient = new MockAPIClient();
        configManager = new ConfigManagerImpl() {
            @Override
            protected String getCustomConfigPathForUserDomain(EdOrgKey key) {
                return "aa";
            }
            @Override
            public ConfigMap getCustomConfig(String token, EdOrgKey edOrgKey) {
                return null;
            }
        };
        configManager.setDriverConfigLocation("config");

        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(mockClient);
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
        Config config = configManager.getComponentConfig("1", new EdOrgKey("1"), "gridSample");
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
            configManager.getComponentConfig(null, new EdOrgKey("1"), "fakeConfigId");
        } catch (Throwable t) {
            Assert.assertEquals("Unable to read local custom config for fakeConfigId", t.getMessage());
        }
    }

    @Test
    public void testConfigLocation() {
        String location = "config";
        configManager.setDriverConfigLocation(location);
        Assert.assertTrue(configManager.getDriverConfigLocation("x").contains(location));
    }
}
