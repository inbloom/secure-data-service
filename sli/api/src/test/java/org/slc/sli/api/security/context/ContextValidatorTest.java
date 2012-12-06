/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.security.context;

import com.sun.jersey.spi.container.ContainerRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.security.SLIPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

/**
 * Tests for ContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class ContextValidatorTest {

    @Autowired
    private ContextValidator contextValidator;

    private ContainerRequest containerRequest;
    private SLIPrincipal principal;

    @Before
    public void setUp() {
        containerRequest = Mockito.mock(ContainerRequest.class);
        principal = Mockito.mock(SLIPrincipal.class);
    }

    @Test
    public void testDenyWritingOutsideOfEdOrgHierarchy() throws Exception {

        when(containerRequest.getMethod()).thenReturn("UPDATE");
        Method validateEdOrgWrite = contextValidator.getClass().getDeclaredMethod("isValidForEdOrgWrite", ContainerRequest.class, SLIPrincipal.class);
        validateEdOrgWrite.setAccessible(true);

        Boolean isValid = (Boolean) validateEdOrgWrite.invoke(contextValidator, new Object[]{containerRequest, principal});

        Assert.assertFalse("should fail validation", isValid.booleanValue());

    }

}
