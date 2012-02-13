package org.slc.sli.api.security.oauth;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * 
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationServiceTest {

    @Autowired
    private ApplicationService resource;

    private EntityService service;
    private EntityBody app;

    private static final int STATUS_CREATED = 201;

    @Before
    public void setUp() throws Exception {

        app = new EntityBody();
        app.put("client_type", "PUBLIC");
        app.put("redirect_uri", "https://slidev.org");
        app.put("description", "blah blah blah");
        app.put("name", "TestApp");
        // app.put("client_id", "Eg6eseKRzN");
        // app.put("client_secret",
        // "FqXBla26esaLGcVfhlcLnePOEmeHuez7JpmnR9pLg6RkThqF");

        service = mock(EntityService.class);

        resource.setService(service);
    }

    @Test
    public void testCreate() {
        // test create during dup check
        Mockito.when(
                service.list(Mockito.eq(0), Mockito.eq(1), Mockito.anyString()))
                .thenReturn(new ArrayList<String>());

        Response resp = resource.createApplication(app);
        assertEquals(STATUS_CREATED, resp.getStatus());
        // EntityBody body = (EntityBody) resp.getEntity();
        // assertTrue("Client id set",
        // body.get("client_id").toString().length() == 10);
        // assertTrue("Client secrete set", body.get("client_secret").toString()
        // .length() == 48);
    }

}
