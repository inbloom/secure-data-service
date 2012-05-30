package org.slc.sli.unit.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.slc.sli.web.controller.DataController;
import org.slc.sli.web.entity.SafeUUID;

/**
 * Testing data controller
 *
 * @author ccheng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class DataControllerTest {

    private final String componentId = "abc01";
    private final String rawId = "abcd-abcd-abcd-abcd-abcd";

    private MockHttpServletRequest request;
    private DataController dataController;
    private SafeUUID id;

    // partially mock CustomizationAssemblyFactoryImpl
    CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {

        @Override
        public GenericEntity getDataComponent(String componentId, Object entityKey) {
            GenericEntity simpleEntity = new GenericEntity();
            simpleEntity.put("id", componentId);
            return simpleEntity;
        }
    };

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        dataController = new DataController();
        id = new SafeUUID(rawId);
    }

    @Test
    public void testHandleValidId() throws Exception {

        dataController.setCustomizedDataFactory(dataFactory);

        GenericEntity res = dataController.handle(componentId, id, request);
        Assert.assertEquals(componentId, res.getId());
    }
}
