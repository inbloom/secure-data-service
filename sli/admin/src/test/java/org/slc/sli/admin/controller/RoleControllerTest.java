package org.slc.sli.admin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.admin.client.RESTClient;
import org.slc.sli.admin.test.bootstrap.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 *
 * @author scole
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/commonCtx.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RoleControllerTest {

    private RESTClient rest;
    private RoleController controller;

    @Value("${roles.json}")
    private String rolesJSON;

    @Before
    public void init() {
        this.rest = Mockito.mock(RESTClient.class);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(rolesJSON).getAsJsonArray();
        Mockito.when(this.rest.getRoles(null)).thenReturn(jsonArray);
        controller = new RoleController();
        controller.setRESTClient(rest);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetRoles() throws IOException {

        ModelAndView view = Mockito.mock(ModelAndView.class);
        HttpSession session = Mockito.mock(HttpSession.class);

        final Map<String, Object> values = new HashMap<String, Object>();

        Mockito.when(view.addObject(Mockito.anyString(), Mockito.anyCollection())).thenAnswer(
                new Answer<ModelAndView>() {

                    @Override
                    public ModelAndView answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        values.put(args[0].toString(), args[1]);
                        return (ModelAndView) invocation.getMock();
                    }

                });

        controller.getRoles(view, session);

        Assert.assertTrue("There must be a roleJsonData entry", values.containsKey("roleJsonData"));
        List<Map<String, Object>> roles = (List<Map<String, Object>>) values.get("roleJsonData");
        for (Map<String, Object> role : roles) {
            if (role.get("name").equals("Educator")) {
                Assert.assertEquals("Leader should have blank for restricted data", "None", role.get("restricted"));
                Assert.assertEquals("Leader should have R for general data", "R", role.get("general"));
                Assert.assertEquals("Leader should have true for aggregate data", true, role.get("aggregate"));
            }
            if (role.get("name").equals("Leader")) {
                Assert.assertEquals("Leader should have R for restricted data", "R", role.get("restricted"));
                Assert.assertEquals("Leader should have R for general data", "R", role.get("general"));
                Assert.assertEquals("Leader should have true for aggregate data", true, role.get("aggregate"));
            }
            if (role.get("name").equals("IT Administrator")) {
                Assert.assertEquals("Leader should have R/W for restricted data", "R/W", role.get("restricted"));
                Assert.assertEquals("Leader should have R/W for general data", "R/W", role.get("general"));
                Assert.assertEquals("Leader should have true for aggregate data", true, role.get("aggregate"));
            }
        }
    }

}
