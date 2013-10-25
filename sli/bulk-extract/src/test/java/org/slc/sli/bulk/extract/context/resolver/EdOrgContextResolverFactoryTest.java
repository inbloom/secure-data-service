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
package org.slc.sli.bulk.extract.context.resolver;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.bulk.extract.context.resolver.impl.EducationOrganizationContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.SimpleEntityTypeContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentDirectRelatedContextResolver;

@RunWith(MockitoJUnitRunner.class)
public class EdOrgContextResolverFactoryTest {
    
    @InjectMocks
    EdOrgContextResolverFactory factory = new EdOrgContextResolverFactory();
    
    @Mock
    SimpleEntityTypeContextResolver edOrgContextResolver;
    
    @Mock
    StudentDirectRelatedContextResolver studentDirectContextResolver;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        factory.init();
    }

    @Test
    public void test() {
        assertNull(factory.getResolver("doesn't exist"));
        ContextResolver resolver = factory.getResolver("educationOrganization");
        assertTrue(resolver instanceof SimpleEntityTypeContextResolver);
    }
    
    @Test
    public void testStudentParent() {
        assertTrue(factory.getResolver("studentParentAssociation") instanceof StudentDirectRelatedContextResolver);
    }
    
}
