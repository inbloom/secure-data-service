package org.slc.sli.api.resources;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.WebContextTestExecutionListener;

/**
 * Provides common annotations for any tests that are testing resources that are exposed in a
 * RESTful manner
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public abstract class ResourceTest {
    
    public UriInfo buildMockUriInfo() throws Exception {
        UriInfo mock = mock(UriInfo.class);
        UriBuilder builder = mock(UriBuilder.class);
        when(mock.getAbsolutePathBuilder()).thenReturn(builder);
        when(mock.getBaseUriBuilder()).thenReturn(builder);
        when(mock.getRequestUriBuilder()).thenReturn(builder);
        when(builder.path(anyString())).thenReturn(builder);
        when(builder.path(any(Class.class))).thenReturn(builder);
        URI uri = new URI("/newPath");
        when(builder.build()).thenReturn(uri);
        
        return mock;
    }
}
