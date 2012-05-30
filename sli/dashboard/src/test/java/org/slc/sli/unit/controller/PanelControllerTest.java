package org.slc.sli.unit.controller;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Condition;
import org.slc.sli.entity.Config.Data;
import org.slc.sli.entity.Config.Item;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.web.controller.PanelController;
import org.slc.sli.web.entity.SafeUUID;

/**
 * Testing data controller
 *
 * @author ccheng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class PanelControllerTest {

    private final String componentId = "abc01";
    private final String rawId = "abcd-abcd-abcd-abcd-abcd";

    private MockHttpServletRequest request;
    private PanelController panelController;
    private SafeUUID id;

    // partially mock CustomizationAssemblyFactoryImpl
    CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {

        @Override
        public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey, boolean lazyOverride) {
            GenericEntity simpleEntity = new GenericEntity();
            simpleEntity.put("id", componentId);

            Type type = null;
            Condition condition = null;
            Item[] items = null;

            Map<String, Object> params = null;
            Data simpleData = new Data("entity", componentId, true, params);

            Config simpleViewConfig = new Config(componentId, "parent", "name", type, condition, simpleData, items,
                    "root");
            ModelAndViewConfig simpleModelAndViewConfig = new ModelAndViewConfig();

            simpleModelAndViewConfig.addData(componentId, simpleEntity);
            simpleModelAndViewConfig.addConfig(componentId, simpleViewConfig);

            return simpleModelAndViewConfig;
        }
    };

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        panelController = new PanelController();
        id = new SafeUUID(rawId);
    }

    @Test
    public void testHandleValidId() throws Exception {

        // expected result
        GenericEntity expectedEntity = new GenericEntity();
        expectedEntity.put("id", componentId);

        panelController.setCustomizedDataFactory(dataFactory);

//        GenericEntity res = panelController.handle(componentId, id, request);
//        Assert.assertEquals(expectedEntity, res.get("data"));

    }
}
