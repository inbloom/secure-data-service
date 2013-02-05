/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.RightCheckFilterFactory.RightCheckResourceFilter;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.access.AccessDeniedException;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ResourceFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@DirtiesContext
public class RightsAllowedAnnotationTest {
    
    @Autowired
    RightCheckFilterFactory factory;
    
    @Autowired
    private SecurityContextInjector injector;
    
    AbstractMethod method = null;
    
    public static class TestClass extends AbstractResource {
               
        public TestClass(Class<?> resourceClass) {
            super(resourceClass);
        }

        @RightsAllowed({Right.ADMIN_ACCESS})
        public void oneRight() {
            
        }
        
        @RightsAllowed({Right.ADMIN_ACCESS, Right.READ_GENERAL})
        public void twoRight() {
            
        }
        
        @RightsAllowed(any=true)
        public void allowAll() {
            
        }
    }
    
    private RightsAllowed getAdminRightAnnotation() throws SecurityException, NoSuchMethodException {
        return TestClass.class.getMethod("oneRight", new Class[] {}).getAnnotation(RightsAllowed.class);
    }
    
    private RightsAllowed getMultiRightAnnotation() throws SecurityException, NoSuchMethodException {
        return TestClass.class.getMethod("twoRight", new Class[] {}).getAnnotation(RightsAllowed.class);
    }
    
    private RightsAllowed getAllowAnyAnnotation() throws SecurityException, NoSuchMethodException {
        return TestClass.class.getMethod("allowAll", new Class[] {}).getAnnotation(RightsAllowed.class);
    }
    
    
    private ContainerRequest getMockContainerRequest() throws URISyntaxException {
        ContainerRequest req = Mockito.mock(ContainerRequest.class);
        Mockito.when(req.getRequestUri()).thenReturn(new URI("http://blah"));
        return req;
    }
    
    @Before
    public void setup() {
        SecurityContextHolder.clearContext();
        method = Mockito.mock(AbstractMethod.class);
        AbstractResource res = new TestClass(this.getClass());
        Mockito.when(method.getResource()).thenReturn(res);
    }
    
    @Test(expected=InsufficientAuthenticationException.class)
    public void testNotAuthenticated() throws Exception {
        injector.setAnonymousContext();
        RightsAllowed allowed = getAdminRightAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        filter.filter(getMockContainerRequest());
    }
    
   
    
    @Test
    public void testAdminUser() throws Exception {
        injector.setLeaAdminContext();
        
        RightsAllowed allowed = getAdminRightAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        //Should pass the request through without any error
        Assert.assertNotNull(filter.filter(getMockContainerRequest()));
    }
    
    @Test
    public void testMultiAnnotation() throws Exception {
        injector.setLeaAdminContext();
        
        RightsAllowed allowed = getMultiRightAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        //Should pass the request through without any error
        Assert.assertNotNull(filter.filter(getMockContainerRequest()));
    }
    
    @Test(expected=AccessDeniedException.class)
    public void testEducatorUserOnAdminResource() throws Exception {
        injector.setEducatorContext();
        
        RightsAllowed allowed = getAdminRightAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        filter.filter(getMockContainerRequest());
    }
    
    @Test
    public void testAllowAnyWithAuthenticatedUser() throws Exception {
        injector.setEducatorContext();
        
        RightsAllowed allowed = getAllowAnyAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        //Should pass the request through without any error
        Assert.assertNotNull(filter.filter(getMockContainerRequest()));
    }
    
    @Test(expected=InsufficientAuthenticationException.class)
    public void testAllowAnyWithoutAuthenticatedUser() throws Exception {
        injector.setAnonymousContext();
        RightsAllowed allowed = getAllowAnyAnnotation();
        Mockito.when(method.getAnnotation(RightsAllowed.class)).thenReturn(allowed);
        
        List<ResourceFilter> filters = factory.create(method);
        Assert.assertEquals(1, filters.size());
        RightCheckResourceFilter filter = (RightCheckResourceFilter) filters.get(0);
        
        filter.filter(getMockContainerRequest());
    }
}
