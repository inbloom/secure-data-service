package org.slc.sli.unit.manager;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.client.MockAPIClient;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.entity.Config;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.EntityManager;


/**
 * Unit tests for the StudentManager class.
 *
 */
public class ConfigManagerTest {

    ConfigManager configManager;
    MockAPIClient mockClient;

    @Before
    public void setup() {
        mockClient = new MockAPIClient();
        configManager = new ConfigManager();
        configManager.setApiClient(mockClient);
        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(mockClient);
        configManager.setEntityManager(entityManager);
    }

    @Test
    public void testGetConfigSet() {

        ViewConfigSet configSet = configManager.getConfigSet("lkim");
        assertEquals("IL_3-8_ELA", configSet.getViewConfig().get(0).getName());
    }

    @Test
    public void testGetConfigSetMissing() {

        // look for a config set that doesn't exist
        ViewConfigSet configSet = configManager.getConfigSet("not_there");
        assertNull(configSet);
    }

    @Test
    public void testGetConfig() {

        ViewConfig config = configManager.getConfig("lkim", "IL_3-8_ELA");
        assertEquals(3, config.getDisplaySet().size());
    }

    @Test
    public void testGetConfigMissing() {

        ViewConfig config = configManager.getConfig("not_there", "IL_3-8_ELA");
        assertNull(config);

        ViewConfig config2 = configManager.getConfig("lkim", "not_there");
        assertNull(config2);
    }

    
    /**
     * Test get config to return expected
     */
    @Test
    public void testConfigFields() {
        Config config = configManager.getComponentConfig("1", "gridSample");
        Assert.assertEquals("gridSample", config.getId());
        Assert.assertEquals("attendance", config.getRoot());
        Assert.assertEquals(config.getName(), "Grid");
        Assert.assertEquals("studentAttendance", config.getData().getEntityRef());
        Assert.assertEquals("studentAttendance", config.getData().getAlias());
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
        Assert.assertEquals("Data [entityRef=studentAttendance, entityAlias=studentAttendance, params={}]", config.getData().toString());
        Assert.assertEquals("Condition [field=x, value=[x, y, z]]", condition.toString());
        Assert.assertEquals("ViewItem [width=90, type=string, color=null, style=null, formatter=null, params=null]", items[0].toString());
    }
}
